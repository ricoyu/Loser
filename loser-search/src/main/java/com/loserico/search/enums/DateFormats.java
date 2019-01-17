package com.loserico.search.enums;

public enum DateFormats {
	
	DEFAULT("strict_date_optional_time||epoch_millis"),

	/**
	 * A formatter for the number of milliseconds since the epoch.
	 */
	EPOCH_MILLIS,

	/**
	 * A formatter for the number of seconds since the epoch.
	 */
	EPOCH_SECOND,
	
	/**
	 * A generic ISO datetime parser where the date is mandatory and the time is optional.
	 */
	DATE_OPTIONAL_TIME,
	
	/**
	 * A generic ISO datetime parser where the date is mandatory and the time is optional.
	 */
	STRICT_DATE_OPTIONAL_TIME,
	
	/**
	 * A formatter for a four digit year, two digit month of year, and two digit day of month
	 */
	YEAR_MONTH_DAY;

	private String format = "yyyy-MM-dd HH:mm:ss";

	private DateFormats() {
		this.format = null;
	}

	private DateFormats(String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return format == null ? name().toLowerCase() : format;
	}

}
