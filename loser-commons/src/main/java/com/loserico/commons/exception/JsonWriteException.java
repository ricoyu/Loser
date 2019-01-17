package com.loserico.commons.exception;

public class JsonWriteException extends RuntimeException {

	private static final long serialVersionUID = 4250721255160791908L;

	public JsonWriteException() {
	}

	public JsonWriteException(String message) {
		super(message);
	}

	public JsonWriteException(Throwable cause) {
		super(cause);
	}

	public JsonWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonWriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
