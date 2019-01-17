package com.loserico.orm.jpa.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BasicPredicate extends AbstractPredicate {

	private Object propertyValue;
	private BasicMatchMode basicMatchMode = BasicMatchMode.EQ;

	public BasicPredicate(String propertyName, Object propertyValue) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
	}

	public BasicPredicate(String propertyName, Object propertyValue, BasicMatchMode basicMatchMode) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
		this.basicMatchMode = basicMatchMode;
	}

	@SuppressWarnings("rawtypes")
	public Predicate toPredicate(CriteriaBuilder criteriaBuilder, Root root) {
		Predicate predicate = null;
		switch (basicMatchMode) {
		case EQ:
			predicate = criteriaBuilder.equal(root.get(getPropertyName()), propertyValue);
			break;
		case NE:
			predicate = criteriaBuilder.notEqual(root.get(getPropertyName()), propertyValue);
			break;
		default:
			break;
		}
		return predicate;
	}

}
