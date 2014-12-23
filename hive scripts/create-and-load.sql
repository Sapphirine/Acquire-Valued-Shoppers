create table trainhistory(id INT, chain INT, offer INT, market INT, offerdate TIMESTAMP) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bigdata/Downloads/data/trainHistory.csv' INTO TABLE trainhistory;


create table testhistory(id INT, chain INT, offer INT, market INT, offerdate TIMESTAMP) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bigdata/Downloads/data/testHistory.csv' INTO TABLE testhistory;


create table offers(offer INT, category INT, quantity INT, company BIGINT, offervalue INT, brand INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bigdata/Downloads/offers.csv' INTO TABLE offers;


create table reduced(id INT, chain INT, dept INT, category INT, company BIGINT, brand INT, date TIMESTAMP, productsize INT, productmeasure STRING, purchasequantity INT, purchaseamount FLOAT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bigdata/Downloads/data/reduced.csv' INTO TABLE reduced;