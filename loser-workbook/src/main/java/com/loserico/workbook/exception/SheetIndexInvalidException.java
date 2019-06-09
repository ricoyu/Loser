package com.loserico.workbook.exception;

public class SheetIndexInvalidException extends RuntimeException {

	private static final long serialVersionUID = 1979711868433685408L;

	public SheetIndexInvalidException() {
	}

	public SheetIndexInvalidException(String message) {
		super(message);
	}

	public SheetIndexInvalidException(Throwable cause) {
		super(cause);
	}

	public SheetIndexInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public SheetIndexInvalidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
