# iotApp


## Create the cassandra environment locally.

Install cassandra on your local by following: https://www.datastax.com/2012/01/working-with-apache-cassandra-on-mac-os-x

## Sample data preparation for demonstration purpose

cqlsh> CREATE KEYSPACE vendingSpace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor' : 3};
cqlsh> USE vendingSpace;

cqlsh:vendingspace> CREATE TABLE daily_popular_products (  YYYYMMDD_date TEXT,  counter_with_product_code TEXT,  PRIMARY KEY (YYYYMMDD_date, counter_with_product_code) ) WITH CLUSTERING ORDER BY (counter_with_product_code DESC);

cqlsh:vendingspace> INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0001_productA');
            
cqlsh:vendingspace> INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0002_productA');

cqlsh:vendingspace> INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0003_productA');

cqlsh:vendingspace> INSERT INTO daily_popular_products (YYYYMMDD_date, counter_with_product_code) VALUES ('20180310','0001_productB');


## Note that the values are sorted by the number of times a product was purchased

cqlsh:vendingspace> select * from daily_popular_products;

 yyyymmdd_date | counter_with_product_code

---------------+---------------------------

      20180310 |             0003_productA
      
      20180310 |             0002_productA
      
      20180310 |             0001_productB
      
      20180310 |             0001_productA

(4 rows)

cqlsh:vendingspace> CREATE TABLE daily_active_consumers (  YYYYMMDD_date TEXT, consumer_id TEXT,  PRIMARY KEY (YYYYMMDD_date, consumer_id) );

cqlsh:vendingspace> INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','57cc0ddd990428000bc6f6ce');

cqlsh:vendingspace> INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','58094f04598e4d000c9b1301');

cqlsh:vendingspace> INSERT INTO daily_active_consumers (YYYYMMDD_date, consumer_id) VALUES ('20180310','58789e27b9b21c000c54b362');

cqlsh:vendingspace> select * from daily_active_consumers;

 yyyymmdd_date | consumer_id

---------------+--------------------------

      20180310 | 57cc0ddd990428000bc6f6ce
      
      20180310 | 58094f04598e4d000c9b1301
      
      20180310 | 58789e27b9b21c000c54b362

(3 rows)


## Note that we get active consumers for a particular day just by doing a simple count on a row key 

cqlsh:vendingspace> select count(*) from daily_active_consumers where YYYYMMDD_date = '20180310';

 count
-------
     3

(1 rows)



