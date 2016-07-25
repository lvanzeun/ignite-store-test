package org.mycorp.ignite;

import java.sql.Types;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMemoryMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStore;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.H2Dialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.h2.jdbcx.JdbcConnectionPool;
import org.mycorp.ignite.model.Account;
import org.mycorp.ignite.model.AccountKey;
import org.mycorp.ignite.model.Balance;
import org.mycorp.ignite.model.BalanceKey;
import org.mycorp.ignite.model.Customer;
import org.mycorp.ignite.model.CustomerKey;

/**
 * Application starter utility class.
 *
 */
public final class ApplicationStarter {

	private static final String H2_PWD = "";

	private static final String H2_USERNAME = "sa";

	private static final String H2_TCP_URL = "jdbc:h2:tcp://localhost/~/ExampleDb";

	/**
	 * Name of the single cache
	 */
	public static final String CACHE_NAME = "aSingleCache";

	/**
	 * Switch ON/OFF persistence configuration
	 */
	private static final boolean WITH_PERSISTENCE = true;

	/**
	 * On purpose.
	 */
	private ApplicationStarter() {

	}

	private static final class CacheJdbcPojoStoreExampleFactory extends CacheJdbcPojoStoreFactory<Object, Object> {

		private static final long serialVersionUID = -6996777135000989868L;

		/** {@inheritDoc} */
		@SuppressWarnings("deprecation")
		@Override
		public CacheJdbcPojoStore<Object, Object> create() {
			setDataSource(JdbcConnectionPool.create(H2_TCP_URL, H2_USERNAME, H2_PWD));

			return super.create();
		}
	}

	/**
	 * Start the grid, configure and create the cache.
	 * 
	 * @return The local Ignite node instance.
	 */
	public static Ignite start() {

		Ignition.setClientMode(true);

		// Look for affinity functions
		Ignite ignite = Ignition.start();

		CacheConfiguration<Object, Object> ccfg = new CacheConfiguration<>(CACHE_NAME);
		ccfg.setIndexedTypes(AccountKey.class, Account.class, BalanceKey.class, Balance.class, CustomerKey.class,
				Customer.class);

		ccfg.setMemoryMode(CacheMemoryMode.OFFHEAP_TIERED);
		ccfg.setCacheMode(CacheMode.REPLICATED);
		ccfg.setStatisticsEnabled(true);

		if (WITH_PERSISTENCE) {
			configureForPersistence(ccfg);
		}

		IgniteCache<Object, Object> zeCache = ignite.getOrCreateCache(ccfg);

		populateCache(zeCache);

		return ignite;
	}

	private static void configureForPersistence(CacheConfiguration<Object, Object> ccfg) {

		// Create store.
		CacheJdbcPojoStore<Object, Object> store = new CacheJdbcPojoStore<>();

		ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);

		// Create store factory.
		CacheJdbcPojoStoreExampleFactory storeFactory = new CacheJdbcPojoStoreExampleFactory();
		storeFactory.setDialect(new H2Dialect());

		// Configure JDBC types.
		Collection<JdbcType> jdbcTypes = new ArrayList<>();

		jdbcTypes.add(getAccountJdbcType(CACHE_NAME));
		jdbcTypes.add(getCustomerJdbcType(CACHE_NAME));
		jdbcTypes.add(getBalanceJdbcType(CACHE_NAME));

		store.setTypes(jdbcTypes.toArray(new JdbcType[jdbcTypes.size()]));

		// Configure query entities.
		Collection<QueryEntity> qryEntities = new ArrayList<>();

		qryEntities.add(queryEntityAccount());
		qryEntities.add(queryEntityCustomer());
		qryEntities.add(queryEntityBalance());

		ccfg.setQueryEntities(qryEntities);

		ccfg.setCacheStoreFactory(storeFactory);

		// Configure cache to use store.
		ccfg.setReadThrough(true);
		ccfg.setWriteThrough(true);

		// Enable database batching.
		ccfg.setWriteBehindEnabled(true);
	}

	/**
	 * Fill the cache with dummy data.
	 * 
	 * @param cache
	 *            The cache to fill in.
	 */
	private static void populateCache(IgniteCache<Object, Object> cache) {

		Customer cust1 = new Customer("Cust1", "BE");
		cache.put(cust1.key(), cust1);
		Customer cust2 = new Customer("Cust2", "FR");
		cache.put(cust2.key(), cust2);
		Customer cust3 = new Customer("Cust3", "NL");
		cache.put(cust3.key(), cust3);
		Customer cust4 = new Customer("Cust4", "US");
		cache.put(cust4.key(), cust4);

		Account acc1 = new Account("Acc1", "Cust1");
		cache.put(acc1.key(), acc1);
		Account acc2 = new Account("Acc2", "Cust1");
		cache.put(acc2.key(), acc2);
		Account acc3 = new Account("Acc3", "Cust2");
		cache.put(acc3.key(), acc3);
		Account acc4 = new Account("Acc4", "Cust3");
		cache.put(acc4.key(), acc4);
		Account acc5 = new Account("Acc5", "Cust4");
		cache.put(acc5.key(), acc5);

		Balance bal1 = new Balance(LocalDate.of(2016, Month.JANUARY, 1), 100.0, "Acc1");
		cache.put(bal1.key(), bal1);
		Balance bal2 = new Balance(LocalDate.of(2016, Month.JANUARY, 1), 200.0, "Acc2");
		cache.put(bal2.key(), bal2);
		Balance bal3 = new Balance(LocalDate.of(2016, Month.JANUARY, 2), 201.0, "Acc2");
		cache.put(bal3.key(), bal3);
		Balance bal4 = new Balance(LocalDate.of(2016, Month.JANUARY, 1), 300.0, "Acc3");
		cache.put(bal4.key(), bal4);
		Balance bal5 = new Balance(LocalDate.of(2016, Month.JANUARY, 1), 400.0, "Acc4");
		cache.put(bal5.key(), bal5);
		Balance bal6 = new Balance(LocalDate.of(2016, Month.JANUARY, 1), 500.0, "Acc5");
		cache.put(bal6.key(), bal6);
		Balance bal7 = new Balance(LocalDate.of(2016, Month.JANUARY, 2), 501.0, "Acc5");
		cache.put(bal7.key(), bal7);
	}

	private static JdbcType getAccountJdbcType(String cacheName) {

		JdbcType jdbcType = new JdbcType();

		jdbcType.setCacheName(cacheName);
		jdbcType.setDatabaseSchema("PUBLIC");
		jdbcType.setDatabaseTable("PERSON");
		jdbcType.setKeyType("org.mycorp.ignite.model.AccountKey");
		jdbcType.setValueType("org.mycorp.ignite.model.Account");

		// Key fields for PERSON.
		List<JdbcTypeField> keys = new ArrayList<>();
		keys.add(new JdbcTypeField(Types.VARCHAR, "REFERENCE", String.class, "reference"));
		jdbcType.setKeyFields(keys.toArray(new JdbcTypeField[keys.size()]));

		// Value fields for PERSON.
		Collection<JdbcTypeField> vals = new ArrayList<>();
		vals.add(new JdbcTypeField(Types.VARCHAR, "REFERENCE", String.class, "reference"));
		vals.add(new JdbcTypeField(Types.VARCHAR, "OWNER", String.class, "ownerName"));
		jdbcType.setValueFields(vals.toArray(new JdbcTypeField[vals.size()]));

		return jdbcType;
	}

	private static JdbcType getCustomerJdbcType(String cacheName) {

		JdbcType jdbcType = new JdbcType();

		jdbcType.setCacheName(cacheName);
		jdbcType.setDatabaseSchema("PUBLIC");
		jdbcType.setDatabaseTable("CUSTOMER");
		jdbcType.setKeyType("org.mycorp.ignite.model.CustomerKey");
		jdbcType.setValueType("org.mycorp.ignite.model.Customer");

		// Key fields for PERSON.
		List<JdbcTypeField> keys = new ArrayList<>();
		keys.add(new JdbcTypeField(Types.VARCHAR, "NAME", String.class, "name"));
		jdbcType.setKeyFields(keys.toArray(new JdbcTypeField[keys.size()]));

		// Value fields for PERSON.
		Collection<JdbcTypeField> vals = new ArrayList<>();
		vals.add(new JdbcTypeField(Types.VARCHAR, "NAME", String.class, "name"));
		vals.add(new JdbcTypeField(Types.VARCHAR, "COUNTRYISOCODE", String.class, "countryIsoCode"));
		jdbcType.setValueFields(vals.toArray(new JdbcTypeField[vals.size()]));

		return jdbcType;
	}

	private static JdbcType getBalanceJdbcType(String cacheName) {

		JdbcType jdbcType = new JdbcType();

		jdbcType.setCacheName(cacheName);
		jdbcType.setDatabaseSchema("PUBLIC");
		jdbcType.setDatabaseTable("BALANCE");
		jdbcType.setKeyType("org.mycorp.ignite.model.BalanceKey");
		jdbcType.setValueType("org.mycorp.ignite.model.Balance");

		// Key fields for PERSON.
		List<JdbcTypeField> keys = new ArrayList<>();
		keys.add(new JdbcTypeField(Types.BIGINT, "ID", Long.class, "id"));
		jdbcType.setKeyFields(keys.toArray(new JdbcTypeField[keys.size()]));

		// Value fields for PERSON.
		Collection<JdbcTypeField> vals = new ArrayList<>();
		vals.add(new JdbcTypeField(Types.BIGINT, "ID", Long.class, "id"));
		vals.add(new JdbcTypeField(Types.VARCHAR, "ACCOUNTREF", String.class, "accountReference"));
		vals.add(new JdbcTypeField(Types.DATE, "DATE", LocalDate.class, "date"));
		vals.add(new JdbcTypeField(Types.DECIMAL, "VALUE", Double.class, "value"));
		jdbcType.setValueFields(vals.toArray(new JdbcTypeField[vals.size()]));

		return jdbcType;
	}

	private static QueryEntity queryEntityAccount() {

		QueryEntity qryEntity = new QueryEntity();

		qryEntity.setKeyType("org.mycorp.ignite.model.AccountKey");
		qryEntity.setValueType("org.mycorp.ignite.model.Account");

		// Query fields for ACCOUNT.
		LinkedHashMap<String, String> fields = new LinkedHashMap<>();

		fields.put("reference", "String");
		fields.put("ownerName", "String");

		qryEntity.setFields(fields);

		// Indexes for ACCOUNT.
		Collection<QueryIndex> idxs = new ArrayList<>();

		idxs.add(new QueryIndex("REFERENCE", true, "PRIMARY_KEY"));

		qryEntity.setIndexes(idxs);

		return qryEntity;
	}

	private static QueryEntity queryEntityCustomer() {

		QueryEntity qryEntity = new QueryEntity();

		qryEntity.setKeyType("org.mycorp.ignite.model.CustomerKey");
		qryEntity.setValueType("org.mycorp.ignite.model.Customer");

		// Query fields for CUSTOMER.
		LinkedHashMap<String, String> fields = new LinkedHashMap<>();

		fields.put("name", "String");
		fields.put("countryIsoCode", "String");

		qryEntity.setFields(fields);

		// Indexes for CUSTOMER.
		Collection<QueryIndex> idxs = new ArrayList<>();

		idxs.add(new QueryIndex("NAME", true, "PRIMARY_KEY"));

		qryEntity.setIndexes(idxs);

		return qryEntity;
	}

	private static QueryEntity queryEntityBalance() {

		QueryEntity qryEntity = new QueryEntity();

		qryEntity.setKeyType("org.mycorp.ignite.model.BalanceKey");
		qryEntity.setValueType("org.mycorp.ignite.model.Balance");

		// Query fields for BALANCE.
		LinkedHashMap<String, String> fields = new LinkedHashMap<>();

		fields.put("id", "Long");
		fields.put("countryIsoCode", "String");
		fields.put("accountReference", "String");
		fields.put("date", "LocalDate");
		fields.put("value", "Double");

		qryEntity.setFields(fields);

		// Indexes for BALANCE.
		Collection<QueryIndex> idxs = new ArrayList<>();

		idxs.add(new QueryIndex("ID", true, "PRIMARY_KEY"));

		qryEntity.setIndexes(idxs);

		return qryEntity;
	}
}
