package org.mycorp.ignite.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public final class Balance implements Serializable {

	private static final long serialVersionUID = 5343149889037593480L;

    private static final AtomicLong IDGEN = new AtomicLong();

	@QuerySqlField(orderedGroups={@QuerySqlField.Group(
		    name = "id_accountReference_idx", order = 0, descending = true)})
	private Long id;

	 @QuerySqlField(index = true, orderedGroups={@QuerySqlField.Group(
			    name = "id_accountReference_idx", order = 1)})	
	private String accountReference;

	@QuerySqlField
	private LocalDate date;

	@QuerySqlField
	private Double value;
	
	private transient BalanceKey key;
	
	public Balance(LocalDate date, Double value, String accountReference) {
		super();
		this.id = IDGEN.incrementAndGet();
		this.date = date;
		this.value = value;
		this.accountReference = accountReference;
	}

	public BalanceKey key() {
		if (key == null) {
			key = new BalanceKey(id, accountReference);
			
		}
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
		Balance other = (Balance) obj;
		if (accountReference == null) {
			if (other.accountReference != null)
				return false;
		} else if (!accountReference.equals(other.accountReference))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getAccountReference() {
		return accountReference;
	}

	public LocalDate getDate() {
		return date;
	}

	public Long getId() {
		return id;
	}

	public Double getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountReference == null) ? 0 : accountReference.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public void setAccountReference(String accountReference) {
		this.accountReference = accountReference;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
