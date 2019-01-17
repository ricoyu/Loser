package com.loserico.web.exception;

public class EntityAlreadyExistingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EntityAlreadyExistingException() {
	}

	public EntityAlreadyExistingException(String message) {
		super(message);
	}

	public EntityAlreadyExistingException(Throwable cause) {
		super(cause);
	}

	public EntityAlreadyExistingException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityAlreadyExistingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
