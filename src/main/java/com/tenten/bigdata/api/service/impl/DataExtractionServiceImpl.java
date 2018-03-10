package com.tenten.bigdata.api.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.sql.Dataset;
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
	 * Get the values from conf file.
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
	 * 
	 */
	@Override
	public List<Product> getPopularProducts(Date from, Date to) {

		// SQLContext sql = new SQLContext(getSparkSession());

		// This will do lazy evaluation and will load only when an action is
		// called.
		Dataset<Row> dataset = getSparkSession().read().format("org.apache.spark.sql.cassandra")
				.options(new HashMap<String, String>() {
					private static final long serialVersionUID = 7135179454453952902L;
					{
						put("keyspace", "vendingSpace");
						put("table", "daily_popular_products");
					}
				}).load();

		dataset.createOrReplaceTempView("usertable");
		Dataset<Row> dataset1 = getSparkSession().sql("select * from usertable where username = 'Mat'");
		dataset1.show();

		// get daily_popular_product details

		// load data in parallel for each day between from and to

		// for each day, get the top X products

		// load all the top X popular products for each day in spark (key =
		// product count, value = product name and other details)

		// sort by the product count and get top X

		// return the top X product list
		return null;
	}

	/**
	 * 
	 */
	@Override
	public int getActiveConsumers(Date from, Date to) {

		// get the daily_active_consumers table details

		// load data in parallel for each day between from and to

		// for each day, get the columns and count them

		// add the counts of each data

		// return the count
		return 0;
	}

	private SparkSession getSparkSession() {
		// TODO: lots of spark configurations can be done spark-defaults.conf &
		// spark.conf
		return SparkSession.builder().appName(appName)
				// .config("spark.sql.warehouse.dir", warehouseLocation)
				.config("spark.cassandra.connection.host", cassandraHost)
				.config("spark.cassandra.connection.port", cassandraPort).master(master).getOrCreate();
	}

	private void writeToDB() {
		// NOTE: These table writes are only for the queries given in problem
		// statement. The write can be done either through simple java or spark
		// connector, depending on the way of receiving the data.

		// write to PRODUCT_COUNT table

		//
		// sample update through spark connector
		//

		CassandraConnector connector = CassandraConnector.apply(getSparkSession().sparkContext().conf());

		// get this as input
		String product_code = "";

		// get this as input
		String purchased_at = "";
		try (Session session = connector.openSession()) {
			session.execute(
					"UPDATE product_count SET produce_purchases = produce_purchases + 1 WHERE product_code= ? AND purchased_at = ?",
					product_code, purchased_at);
		}

		// write to DAILY_POPULAR_PRODUCTS table

		// write to DAILY_ACTIVE_CONSUMERS table

	}

}
