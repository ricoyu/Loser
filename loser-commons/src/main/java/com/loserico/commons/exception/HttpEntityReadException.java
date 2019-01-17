package com.loserico.commons.exception;

public class HttpEntityReadException extends RuntimeException {

	private static final long serialVersionUID = 3518929298518115004L;

	public HttpEntityReadException() {
	}

	public HttpEntityReadException(String message) {
		super(message);
	}

	public HttpEntityReadException(Throwable cause) {
		super(cause);
	}

	public HttpEntityReadException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpEntityReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
