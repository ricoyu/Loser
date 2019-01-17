package com.loserico.workbook.excel.exception;
public class ExcelOutputStreamWriteException extends RuntimeException {

	private static final long serialVersionUID = 6058228730834007347L;

	public ExcelOutputStreamWriteException() {
	}

	public ExcelOutputStreamWriteException(String message) {
		super(message);
	}

	public ExcelOutputStreamWriteException(Throwable cause) {
		super(cause);
	}

	public ExcelOutputStreamWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcelOutputStreamWriteException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}