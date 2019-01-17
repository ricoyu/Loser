package com.loserico.orm.jpa.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class IntegerPredicate extends AbstractPredicate {

	private Integer propertyValue;
	private CompareMode compareMode = CompareMode.EQ;

	public IntegerPredicate(String propertyName, Integer propertyValue) {
		setPropertyName(propertyName);
		this.propertyValue = propertyValue;
	}

	public IntegerPredicate(String propertyName, Integer propertyValue, CompareMode compareMode) {
		this(propertyName, propertyValue);
		this.compareMode = compareMode;
	}

	public CompareMode getCompareMode() {
		return compareMode;
	}

	public void setCompareMode(CompareMode compareMode) {
		this.compareMode = compareMode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Predicate toPredicate(CriteriaBuilder criteriaBuilder, Root root) {
		Predicate predicate = null;
		Path path = root.get(getPropertyName());
		switch (compareMode) {
		case GT:
			predicate = criteriaBuilder.gt(path, propertyValue);
			break;
		case GE:
			predicate = criteriaBuilder.ge(path, propertyValue);
			break;
		case EQ:
			predicate = criteriaBuilder.equal(path, propertyValue);
			break;
		case LT:
			predicate = criteriaBuilder.lessThan(path, propertyValue);
			break;
		case LE:
			predicate = criteriaBuilder.lessThanOrEqualTo(path, propertyValue);
			break;
		case NOTEQ:
			predicate = criteriaBuilder.notEqual(path, propertyValue);
		}
		return predicate;
	}

}