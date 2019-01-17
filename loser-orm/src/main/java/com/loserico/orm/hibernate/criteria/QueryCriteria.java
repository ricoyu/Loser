package com.loserico.orm.hibernate.criteria;

import org.hibernate.criterion.Criterion;

/**
 * Each <tt>QueryCriteria</tt> represents a query condition
 * 
 * @author xuehyu
 * @since 07/29/2014
 */
public interface QueryCriteria {

	/**
	 * Convert query criteria identified by this <tt>QueryCriteria</tt> to a
	 * <tt>org.hibernate.criterion.Criterion</tt>
	 * 
	 * @return <tt>Criterion</tt>
	 */
	Criterion toCriterion();
}
