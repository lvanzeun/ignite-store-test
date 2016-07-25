package org.mycorp.ignite.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.lang.IgniteBiInClosure;

public class AccountStore extends CacheStoreAdapter<String, Account> {

	// This method is called whenever "get(...)" methods are called on
	// IgniteCache.
	@Override
	public Account load(String key) {
		try (Connection conn = connection()) {
			try (PreparedStatement st = conn.prepareStatement("select * from ACCOUNTS where reference=?")) {
				st.setString(1, key);

				ResultSet rs = st.executeQuery();

				return rs.next() ? new Account(rs.getString(1), rs.getString(2)) : null;
			}
		} catch (SQLException e) {
			throw new CacheLoaderException("Failed to load: " + key, e);
		}
	}

	// This mehtod is called whenever "put(...)" methods are called on
	// IgniteCache.
	@Override
	public void write(Entry<? extends String, ? extends Account> entry) throws CacheWriterException {
		try (Connection conn = connection()) {
			// Syntax of MERGE statement is database specific and should be
			// adopted for your database.
			// If your database does not support MERGE statement then use
			// sequentially update, insert statements.
			try (PreparedStatement st = conn
					.prepareStatement("merge into ACCOUNTS (reference, ownerName) key (reference) VALUES (?, ?)")) {

				Account val = entry.getValue();

				st.setString(1, val.getReference());
				st.setString(2, val.getOwner());

				st.executeUpdate();
			}
		} catch (SQLException e) {
			throw new CacheWriterException(
					"Failed to write [key=" + entry.getKey() + ", val=" + entry.getValue() + "']'", e);
		}
	}

	// This mehtod is called whenever "remove(...)" methods are called on
	// IgniteCache.
	@Override
	public void delete(Object key) {
		try (Connection conn = connection()) {
			try (PreparedStatement st = conn.prepareStatement("delete from ACCOUNTS where reference=?")) {
				st.setString(1, (String) key);

				st.executeUpdate();
			}
		} catch (SQLException e) {
			throw new CacheWriterException("Failed to delete: " + key, e);
		}
	}

	// This mehtod is called whenever "loadCache()" and "localLoadCache()"
	// methods are called on IgniteCache. It is used for bulk-loading the
	// cache.
	// If you don't need to bulk-load the cache, skip this method.
	@Override
	public void loadCache(IgniteBiInClosure<String, Account> clo, Object... args) {
		if (args == null || args.length == 0 || args[0] == null)
			throw new CacheLoaderException("Expected entry count parameter is not provided.");

		final int entryCnt = (Integer) args[0];

		try (Connection conn = connection()) {
			try (PreparedStatement st = conn.prepareStatement("select * from PERSONS")) {
				try (ResultSet rs = st.executeQuery()) {
					int cnt = 0;

					while (cnt < entryCnt && rs.next()) {
						Account account = new Account(rs.getString(1), rs.getString(2));

						clo.apply(account.getReference(), account);

						cnt++;
					}
				}
			}
		} catch (SQLException e) {
			throw new CacheLoaderException("Failed to load values from cache store.", e);
		}
	}

	// Open JDBC connection.
	private Connection connection() throws SQLException {
		// Open connection to your RDBMS systems (Oracle, MySQL, Postgres,
		// DB2, Microsoft SQL, etc.)
		// In this example we use H2 Database for simplification.
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1");

		conn.setAutoCommit(true);

		return conn;
	}
}
