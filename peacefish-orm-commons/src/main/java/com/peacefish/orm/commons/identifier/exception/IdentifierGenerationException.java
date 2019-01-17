package com.peacefish.orm.commons.identifier.exception;

public class IdentifierGenerationException extends RuntimeException {

	private static final long serialVersionUID = 3658601234373713631L;

	public IdentifierGenerationException() {
	}

	public IdentifierGenerationException(String message) {
		super(message);
	}

	public IdentifierGenerationException(Throwable cause) {
		super(cause);
	}

	public IdentifierGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdentifierGenerationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
