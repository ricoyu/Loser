package com.loserico.orm.hibernate.criteria;

public abstract class AbstractQueryCriteria implements QueryCriteria {

	private String propertyName;

	public enum CompareMode {
		GT,
		GE,
		EQ,
		LT,
		LE,
		NOTEQ;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
}
