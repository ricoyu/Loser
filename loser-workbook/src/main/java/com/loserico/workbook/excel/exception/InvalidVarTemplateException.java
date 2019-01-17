package com.loserico.workbook.excel.exception;
public class InvalidVarTemplateException extends RuntimeException {

	private static final long serialVersionUID = -1404780319518180287L;

	public InvalidVarTemplateException() {
	}

	public InvalidVarTemplateException(String message) {
		super(message);
	}

	public InvalidVarTemplateException(Throwable cause) {
		super(cause);
	}

	public InvalidVarTemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidVarTemplateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}