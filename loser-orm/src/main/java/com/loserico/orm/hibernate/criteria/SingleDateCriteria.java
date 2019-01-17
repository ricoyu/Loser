package com.loserico.orm.hibernate.criteria;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * Default to earlier than specified date
 * 
 * @author xuehyu
 * @since Jul 29, 2014
 *
 */
public class SingleDateCriteria extends DateCriteria {

	private Date specifiedDate;

	private DateMatchMode matchMode = DateMatchMode.EARLIER_THAN;

	public SingleDateCriteria(String propertyName, Date specifiedDate) {
		setPropertyName(propertyName);
		this.specifiedDate = specifiedDate;
		addCandidateMatchMode(DateCriteria.DateMatchMode.EARLIER_THAN, DateCriteria.DateMatchMode.EARLIER_THAN_OR_SAME,
				DateCriteria.DateMatchMode.EXACT, DateCriteria.DateMatchMode.LATER_THAN,
				DateCriteria.DateMatchMode.LATER_THAN_OR_SAME);
	}

	public SingleDateCriteria(String propertyName, Date specifiedDate, DateMatchMode matchMode) {
		this(propertyName, specifiedDate);
		checkDateMatchMode(matchMode);
		this.matchMode = matchMode;
	}

	public Date getSpecifiedDate() {
		return specifiedDate;
	}

	public void setSpecifiedDate(Date specifiedDate) {
		this.specifiedDate = specifiedDate;
	}

	public DateMatchMode getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(DateMatchMode matchMode) {
		this.matchMode = matchMode;
	}

	@Override
	public Criterion toCriterion() {
		Criterion criterion = null;

		switch (matchMode) {
		case EARLIER_THAN:
			criterion = Restrictions.lt(getPropertyName(), this.specifiedDate);
			break;
		case EARLIER_THAN_OR_SAME:
			criterion = Restrictions.le(getPropertyName(), this.specifiedDate);
			break;
		case EXACT:
			criterion = Restrictions.eq(getPropertyName(), this.specifiedDate);
			break;
		case LATER_THAN:
			criterion = Restrictions.gt(getPropertyName(), this.specifiedDate);
			break;
		case LATER_THAN_OR_SAME:
			criterion = Restrictions.ge(getPropertyName(), this.specifiedDate);
		default:
			break;
		}
		return criterion;
	}

}
