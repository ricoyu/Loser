package com.loserico.orm.hibernate.criteria;

import java.security.InvalidParameterException;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class BooleanCriteria extends AbstractQueryCriteria {

	private Boolean propertyValue;
	private CompareMode compareMode = CompareMode.EQ;

	public BooleanCriteria() {

	}

	public BooleanCriteria(String propertyName, Boolean propertyValue) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
	}

	public BooleanCriteria(String propertyName, Boolean propertyValue, CompareMode compareMode) {
		this(propertyName, propertyValue);
		this.setCompareMode(compareMode);
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	@Override
	public Criterion toCriterion() {
		Criterion criterion = null;
		switch (compareMode) {
		case GT:
			criterion = Restrictions.gt(getPropertyName(), propertyValue);
			break;
		case GE:
			criterion = Restrictions.ge(getPropertyName(), propertyValue);
			break;
		case EQ:
			criterion = Restrictions.eq(getPropertyName(), propertyValue);
			break;
		case LT:
			criterion = Restrictions.lt(getPropertyName(), propertyValue);
			break;
		case LE:
			criterion = Restrictions.le(getPropertyName(), propertyValue);
			break;
		case NOTEQ:
			criterion = Restrictions.not(Restrictions.eq(getPropertyName(), propertyValue));
		}
		return criterion;
	}

	public CompareMode getCompareMode() {
		return compareMode;
	}

	public void setCompareMode(CompareMode compareMode) {
		if (compareMode != CompareMode.EQ && compareMode != CompareMode.NOTEQ) {
			throw new InvalidParameterException("BooleanCriteria only accept compareMode EQ or NOTEQ!");
		}
		this.compareMode = compareMode;
	}

}