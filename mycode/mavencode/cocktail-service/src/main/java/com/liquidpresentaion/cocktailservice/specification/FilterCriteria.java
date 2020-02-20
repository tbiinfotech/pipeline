package com.liquidpresentaion.cocktailservice.specification;

public class FilterCriteria {
	private FilterKey key;

	private String operation;

	private Object value;

	public FilterCriteria() {

	 }

	public FilterCriteria(final String key, final String operation, final Object value) {
	  super();
	  this.key = FilterKey.valueOf(key);
	  this.operation = operation;
	  this.value = value;
	 }


	public FilterKey getKey() {
		return key;
	}

	public void setKey(final FilterKey key) {
		this.key = key;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}
}
