# ignite-store-test

1. Start a new H2 instance 
	e.g. java -jar h2-1.4.192.jar
	
2. Connect to a Generic H2 Server
	- JDBC URL 'jdbc:h2:tcp://localhost/~/ExampleDb'
	- user 'sa'
	- pwd - blank password

3. Create required tables


DROP TABLE IF EXISTS PUBLIC.PERSON;

CREATE TABLE PUBLIC.PERSON (
	REFERENCE 		VARCHAR PRIMARY KEY,
	OWNER 			VARCHAR 
);

DROP TABLE  IF EXISTS PUBLIC.CUSTOMER;

CREATE TABLE PUBLIC.CUSTOMER (
	NAME 			VARCHAR	PRIMARY KEY,
	COUNTRYISOCODE 	VARCHAR
);

DROP TABLE  IF EXISTS PUBLIC.BALANCE;

CREATE TABLE PUBLIC.BALANCE (
	ID 				BIGINT 	PRIMARY KEY,
	ACCOUNTREF 		VARCHAR,
	DATE 			DATE,
	VALUE 			DECIMAL
);


4. Start a node launching 'org.mycorp.ignite.NodeStarter' application

5. Run the tests 
	e.g. mvn test