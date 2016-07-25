package org.mycorp.ignite.model;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public final class Account implements Serializable {

	private static final long serialVersionUID = -7606966239488094028L;

	@QuerySqlField(index = true)
	private String reference;

	@QuerySqlField(index = true)
	private String ownerName;
	
	private transient AccountKey key;

	public Account(String reference, String ownerName) {
		super();
		this.reference = reference;
		this.ownerName = ownerName;
	}

	public Account(AccountKey key) {
		super();
		this.reference = key.getReference();
		this.ownerName = key.getOwnerName();
	}

    public AccountKey key() {
        if (key == null)
            key = new AccountKey(reference, ownerName);

        return key;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
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

	public String getOwner() {
		return ownerName;
	}

	public String getReference() {
		return reference;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	public void setOwner(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
