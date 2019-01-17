package com.loserico.orm.hibernate.criteria;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * String criteria, default to anywhere match, can specify a different
 * <tt>org.hibernate.criterion.MatchMode</tt> to match start, end, exact match.
 * 
 * @author xuehyu
 * @since 29/07/2014
 */
public class StringCriteria extends AbstractQueryCriteria {

	private String propertyValue;

	// Indecate if this criteria should be like %xxx%
	private MatchMode matchMode = MatchMode.ANYWHERE;

	public StringCriteria() {

	}

	public StringCriteria(String propertyName, String propertyValue) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
	}

	public StringCriteria(String propertyName, String propertyValue, MatchMode matchMode) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
		this.matchMode = matchMode;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public MatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
	}

	@Override
	public Criterion toCriterion() {
		if (this.matchMode == MatchMode.EXACT) {
			return Restrictions.eq(getPropertyName(), propertyValue);
		}
		return Restrictions.like(getPropertyName(), propertyValue, matchMode);
	}
}
