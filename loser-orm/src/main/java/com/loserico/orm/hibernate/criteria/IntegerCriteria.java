package com.loserico.orm.hibernate.criteria;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * String criteria, default to anywhere match, can specify a different
 * <tt>org.hibernate.criterion.MatchMode</tt> to match start, end, exact match.
 * 
 * @author jinpzhao
 * @since 29/07/2014
 */
public class IntegerCriteria extends AbstractQueryCriteria {

	private Integer propertyValue;
	private CompareMode compareMode = CompareMode.EQ;

	public IntegerCriteria() {

	}

	public IntegerCriteria(String propertyName, Integer propertyValue) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
	}

	public IntegerCriteria(String propertyName, Integer propertyValue, CompareMode compareMode) {
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
		this.compareMode = compareMode;
	}

}
