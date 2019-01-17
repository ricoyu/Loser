package com.loserico.orm.hibernate.criteria;

import java.util.ArrayList;
import java.util.List;

public abstract class DateCriteria extends AbstractQueryCriteria {

	// All acceptable DateMatchMode stored here
	private List<DateMatchMode> candidateMatchModes = new ArrayList<DateCriteria.DateMatchMode>();

	public static enum DateMatchMode {
		// Two dates are exact the same
		EXACT,
		// Date1 is earlier than date2
		EARLIER_THAN,

		EARLIER_THAN_OR_SAME,

		LATER_THAN,

		LATER_THAN_OR_SAME,
		// Include later than or same day as date1, earlier than date2
		BETWEEN
	}

	protected void addCandidateMatchMode(DateMatchMode... matchModes) {
		for (DateMatchMode matchMode : matchModes) {
			candidateMatchModes.add(matchMode);
		}
	}

	/**
	 * To check if provided DateMatchMode is acceptable
	 * 
	 * @return
	 */
	protected void checkDateMatchMode(DateMatchMode matchMode) {
		boolean isLegal = false;
		for (DateMatchMode candidateMatchMode : candidateMatchModes) {
			if (candidateMatchMode.equals(matchMode)) {
				isLegal = true;
				break;
			}
		}

		if(! isLegal) {
			throw new IllegalArgumentException("Can only accept DateMatchMode: " + toCandidateString());
		}
	}

	private String toCandidateString() {
		StringBuilder sBuilder = new StringBuilder();
		for (DateMatchMode dateMatchMode : candidateMatchModes) {
			sBuilder.append(dateMatchMode.toString()).append(",");
		}
		sBuilder.deleteCharAt(sBuilder.length() - 1);

		return sBuilder.toString();
	}
}
