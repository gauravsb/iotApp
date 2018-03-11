package com.tenten.bigdata.api.service.impl;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.sum;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.tenten.bigdata.api.entity.Product;
import com.tenten.bigdata.api.service.DataExtractionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataExtractionServiceImpl implements DataExtractionService {

	/*
	 * Get the values from a configuration file.
	 */
	@Value("${spark.app.name}")
	private String appName;

	@Value("${spark.master}")
	private String master;

	@Value("${cassandra.host}")
	private String cassandraHost;

	@Value("${cassandra.port}")
	private String cassandraPort;

	/**
	 * Get the top 10 popular products for a given day range.
	 */
	@Override
	public List<Product> getPopularProducts(Date from, Date to) {
		
		// use the same spark session throughout
		SparkSession spark = getSparkSession();

		// This will do lazy evaluation. 
		// It will load data only when an action is called.
		Dataset<Row> dataset = spark.read().format("org.apache.spark.sql.cassandra")
				.options(new HashMap<String, String>() {
					private static final long serialVersionUID = 7135179454453952902L;
					{
						put("keyspace", "vendingSpace");
						put("table", "daily_popular_products");
						put("cluster", "clusterName");
					}
				}).load();

		List<String> rowKeys = getRowKeysFromDates(from, to);

		// get daily_popular_product details

		// load data in parallel for each day between from and to
		// for each day, get the top X products
		rowKeys.stream().parallel()
				.forEach(rowKey -> dataset.union(
						spark.sql("select counter_with_product_code from daily_popular_products where YYYYMMDD_date = "
								+ rowKey + " limit 10")));

		// sort by the product count and get top 10
		return dataset.sort(col("counter_with_product_code").desc())
				.limit(10)
				.as(Encoders.STRING())
				.collectAsList()
				.stream()
				.map(counterWithProduct -> 
				new Product(Integer.valueOf((counterWithProduct.split("_")[0]).split("[^0+(?=[1-9])].*")[0])
						,counterWithProduct.split("_")[1]))
				.collect(Collectors.toList());
	}

	/**
	 * Get the unique active consumers between given dates.
	 */
	@Override
	public int getActiveConsumers(Date from, Date to) {
		
		// use the same spark session throughout
		SparkSession spark = getSparkSession();
		
		// get the daily_active_consumers table details
		Dataset<Row> dataset = spark.read().format("org.apache.spark.sql.cassandra")
				.options(new HashMap<String, String>() {
					private static final long serialVersionUID = -958995515055440072L;
					{
						put("keyspace", "vendingSpace");
						put("table", "daily_active_consumers");
						put("cluster", "clusterName");
					}
				}).load();
		
		List<String> rowKeys = getRowKeysFromDates(from, to);
		
		// load data in parallel for each day between from and to
		rowKeys.stream().parallel()
		.forEach(rowKey -> dataset.union(
				spark.sql("select count(*) as active_consumer_count from daily_active_consumers where YYYYMMDD_date = "
						+ rowKey)));

		// add the counts for each day and return
		return dataset.toDF()
				.agg(sum("active_consumer_count"))
				.first()
				.getInt(0);
	}
	

	/**
	 * Get the cassandra row keys between starting and ending date.
	 * 
	 * @param from
	 * @param to
	 * @return List<String>
	 */
	public List<String> getRowKeysFromDates(Date startDate, Date endDate) {
		LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		List<String> rowKeyList = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
			System.out.println(date);
			rowKeyList.add(date.toString());
		}
		return rowKeyList;
	}

	private SparkSession getSparkSession() {
		// NOTE: lots of spark configurations can be done spark-defaults.conf &
		// spark.conf
		return SparkSession.builder()
				.appName(appName)
				.config("spark.cassandra.connection.host", cassandraHost)
				.config("spark.cassandra.connection.port", cassandraPort)
				.master(master)
				.getOrCreate();
	}

	public void writeToDB() {
		// NOTE: These table writes are only for the queries given in problem
		// statement. The write can be done either through simple java or spark
		// connector, depending on the way of receiving the data.
		// We can also do batch insertions for better performance.

		// write to PRODUCT_COUNT, DAILY_POPULAR_PRODUCTS, DAILY_ACTIVE_CONSUMERS table

		//
		// sample update through spark connector
		//

		CassandraConnector connector = CassandraConnector.apply(getSparkSession().sparkContext().conf());

		// get this as input
		String product_code = "";

		// get this as input
		String purchased_at = "";

		// the following shows sample insert/update
		try (Session session = connector.openSession()) {
			session.execute(
					"UPDATE product_count SET produce_purchases = produce_purchases + 1 WHERE product_code= ? AND purchased_at = ?",
					product_code, purchased_at);

			// due to our schema design, the insertion into
			// daily_popular_products will sorted for easy querying
			session.execute(
					"INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0001_productA')");
			session.execute(
					"INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0002_productA')");
			session.execute(
					"INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0003_productA')");
			session.execute(
					"INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0001_productB')");

			session.execute(
					"INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','57cc0ddd990428000bc6f6ce')");
			session.execute(
					"INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','58094f04598e4d000c9b1301')");
			session.execute(
					"INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','58789e27b9b21c000c54b362')");
		}

	}

}
