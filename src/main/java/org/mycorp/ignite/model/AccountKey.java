package org.mycorp.ignite.model;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class AccountKey implements Serializable {
	
	private static final long serialVersionUID = -7772920656939371225L;

	private final String reference;
	
    @AffinityKeyMapped
	private final String ownerName;

	public AccountKey(String reference, String ownerName) {
		super();
		this.reference = reference;
		this.ownerName = ownerName;
	}
	
	public String getReference() {
		return reference;
	}

	public String getOwnerName() {
		return ownerName;
	}
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
		AccountKey other = (AccountKey) obj;
		if (ownerName == null) {
			if (other.ownerName != null)
				return false;
		} else if (!ownerName.equals(other.ownerName))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}
}
