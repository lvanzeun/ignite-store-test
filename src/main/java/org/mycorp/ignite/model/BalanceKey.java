package org.mycorp.ignite.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public final class BalanceKey implements Serializable {
	
	private static final long serialVersionUID = -7154852853054285788L;

	private final long id;
	
	@AffinityKeyMapped
	private final String accountReference;

	public BalanceKey(long id, String accountReference) {
		super();
		this.id = id;
		this.accountReference = accountReference;
	}

	public long getId() {
		return id;
	}

	public String getAccountReference() {
		return accountReference;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountReference == null) ? 0 : accountReference.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		BalanceKey other = (BalanceKey) obj;
		if (accountReference == null) {
			if (other.accountReference != null)
				return false;
		} else if (!accountReference.equals(other.accountReference))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
