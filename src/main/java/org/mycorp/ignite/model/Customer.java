package org.mycorp.ignite.model;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public final class Customer implements Serializable {

	private static final long serialVersionUID = -2488288151595471822L;

	@QuerySqlField(index = true)
	private String name;

	@QuerySqlField
	private String countryIsoCode;
	
	private transient CustomerKey key;
	
	public CustomerKey key() {
        if (key == null)
            key = new CustomerKey(name);

        return key;
    }
	

	public Customer(String name, String countryIsoCode) {
		super();
		this.name = name;
		this.countryIsoCode = countryIsoCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (countryIsoCode == null) {
			if (other.countryIsoCode != null)
				return false;
		} else if (!countryIsoCode.equals(other.countryIsoCode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getCountry() {
		return countryIsoCode;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((countryIsoCode == null) ? 0 : countryIsoCode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public void setCountry(String country) {
		this.countryIsoCode = country;
	}

	public void setName(String name) {
		this.name = name;
	}
}
