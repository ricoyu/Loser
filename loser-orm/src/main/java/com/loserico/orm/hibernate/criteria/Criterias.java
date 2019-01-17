package com.loserico.orm.hibernate.criteria;

import org.hibernate.criterion.MatchMode;

import com.loserico.orm.hibernate.criteria.AbstractQueryCriteria.CompareMode;

public final class Criterias {

	public static QueryCriteria stringCriteria(String propertyName, String propertyValue) {
		return new StringCriteria(propertyName, propertyValue, MatchMode.EXACT);
	}
	
	public static QueryCriteria stringCriteria(String propertyName, String propertyValue, MatchMode matchMode) {
		return new StringCriteria(propertyName, propertyValue, matchMode);
	}
	
	public static QueryCriteria integerCriteria(String propertyName, Integer propertyValue) {
		return new IntegerCriteria(propertyName, propertyValue);
	}
	
	public static QueryCriteria integerCriteria(String propertyName, Integer propertyValue, CompareMode matchMode) {
		return new IntegerCriteria(propertyName, propertyValue);
	}
	
	public static QueryCriteria booleanCriteria(String propertyName, Boolean propertyValue) {
		return new BooleanCriteria(propertyName, propertyValue);
	}
	
	public static QueryCriteria booleanCriteria(String propertyName, Boolean propertyValue, CompareMode matchMode) {
		return new BooleanCriteria(propertyName, propertyValue);
	}
}
