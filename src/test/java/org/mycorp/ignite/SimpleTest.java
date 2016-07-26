package org.mycorp.ignite;

import java.util.List;
import java.util.logging.Logger;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.fest.assertions.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mycorp.ignite.model.Account;
import org.mycorp.ignite.model.AccountKey;
import org.mycorp.ignite.model.Balance;
import org.mycorp.ignite.model.BalanceKey;

public final class SimpleTest {

	private static Logger logger = Logger.getLogger(SimpleTest.class.getName());

	private static Ignite ignite;

	@BeforeClass
	public static void oneTimeSetup() {

		ignite = ApplicationStarter.start();
	}

	@AfterClass
	public static void oneTimeTearDown() {

		ApplicationStopper.stop(ignite);
	}

	@Test
	public void testSqlAggregation() {

		IgniteCache<BalanceKey, Balance> balanceCache = ignite.cache(ApplicationStarter.CACHE_NAME);

		String sql2 = "select acc.reference, cust.name, sum(bal.value) from Balance as bal, Account as acc, Customer as cust "
				+ " where bal.accountReference = acc.reference and cust.name = acc.ownerName "
				+ " group by cust.name, acc.reference order by acc.reference, cust.name";

		int rowCount = 0;
		try (QueryCursor<List<?>> cursor2 = balanceCache.query(new SqlFieldsQuery(sql2))) {

			for (List<?> row : cursor2) {
				rowCount++;
				logger.info("SQLAgg(cursor)=" + row);
			}
		}

		Assertions.assertThat(rowCount).isEqualTo(5);

		List<?> allResults = balanceCache.query(new SqlFieldsQuery(sql2)).getAll();

		Assertions.assertThat(allResults).isNotEmpty();
	}

	@Test
	public void testSqlJoin() {

		IgniteCache<AccountKey, Account> accountCache = ignite.cache(ApplicationStarter.CACHE_NAME);

		// Get all the account references for a specific person
		String sql = "select acc.reference from Account as acc, Customer as cust "
				+ "where cust.name = acc.ownerName and cust.name = ?";

		int rowCount = 0;
		try (QueryCursor<List<?>> cursor = accountCache.query(new SqlFieldsQuery(sql).setArgs("Cust1"))) {
			for (List<?> row : cursor) {
				rowCount++;
				logger.info("SQLJoin(cursor)=" + row);
			}
		}
		Assertions.assertThat(rowCount).isEqualTo(2);

		List<?> allResults = accountCache.query(new SqlFieldsQuery(sql).setArgs("Cust1")).getAll();

		Assertions.assertThat(allResults).isNotEmpty();
	}
}
