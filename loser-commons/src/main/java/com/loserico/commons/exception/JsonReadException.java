package com.loserico.commons.exception;

public class JsonReadException extends Exception {

	private static final long serialVersionUID = -8006985041873358948L;

	public JsonReadException() {
	}

	public JsonReadException(String message) {
		super(message);
	}

	public JsonReadException(Throwable cause) {
		super(cause);
	}

	public JsonReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
