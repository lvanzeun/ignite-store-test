package org.mycorp.ignite;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * Simple node starter utility class. 
 * 
 */
public final class NodeStarter {
	
	/**
	 * On purpose.
	 */
	private NodeStarter() {
		
	}

	public static void main(String[] args) {

		IgniteConfiguration cfg = new IgniteConfiguration();

		Ignition.start(cfg);
	}
}
