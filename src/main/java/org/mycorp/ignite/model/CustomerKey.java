package org.mycorp.ignite.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public final class CustomerKey implements Serializable {

	private static final long serialVersionUID = 5987244453274554014L;
	
	@AffinityKeyMapped
	private String name;

	public CustomerKey(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerKey other = (CustomerKey) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
