package com.loserico.workbook.exception;

public class BuilderUncompleteException extends RuntimeException {

	private static final long serialVersionUID = 8492104693313185654L;

	public BuilderUncompleteException() {
	}

	public BuilderUncompleteException(String message) {
		super(message);
	}

	public BuilderUncompleteException(Throwable cause) {
		super(cause);
	}

	public BuilderUncompleteException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuilderUncompleteException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
