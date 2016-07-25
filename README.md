# ignite-store-test

1. Start a new H2 instance 
	e.g. java -jar h2-1.4.192.jar
	
2. Connect to a Generic H2 Server
	- JDBC URL 'jdbc:h2:tcp://localhost/~/ExampleDb'
	- user 'sa'
	- pwd - blank password
	
3. Start a node launching 'org.mycorp.ignite.NodeStarter' application

4. Run the tests 
	e.g. mvn test