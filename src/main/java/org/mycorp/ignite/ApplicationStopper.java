package org.mycorp.ignite;

import java.util.Collection;

import org.apache.ignite.Ignite;

/**
 * Application stopper utility class: clear & destroy all caches
 */
public final class ApplicationStopper {

	public static void stop(Ignite ignite) {

		if (ignite != null) {
			
			clearAllCaches(ignite);

			destroyAllCaches(ignite);

			ignite.close();
		}
	}

	private static void destroyAllCaches(Ignite ignite) {

		Collection<String> cacheNames = ignite.cacheNames();

		for (String name : cacheNames) {

			ignite.cache(name).destroy();
		}
	}

	private static void clearAllCaches(Ignite ignite) {

		Collection<String> cacheNames = ignite.cacheNames();

		for (String name : cacheNames) {

			ignite.cache(name).clear();
		}
	}
}
