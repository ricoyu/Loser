package com.loserico.commons.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 4171840322545027764L;

	private static Pattern pattern = Pattern.compile("^Unable to find\\s*([a-zA-Z0-9_]+)\\s*with id .*");
	
	private String entity;

	public EntityNotFoundException() {
	}

	public EntityNotFoundException(String message) {
		super(message);
		extractEntityName(message);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
		extractEntityName(message);
	}

	public EntityNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		extractEntityName(message);
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	private void extractEntityName(String message) {
		Matcher matcher = pattern.matcher("Unable to find  Staff_asd   with id   123 ");
		if(matcher.matches()) {
			this.entity = matcher.group(1);
		}
	}
}
