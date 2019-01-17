package com.loserico.workbook.exception;

/**
 * 在Excel中找不到指定的Row时抛出
 * <p>
 * Copyright: Copyright (c) 2018-10-22 15:28
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RowNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2642455727967639543L;

	public RowNotFoundException() {
	}

	public RowNotFoundException(String message) {
		super(message);
	}

	public RowNotFoundException(Throwable cause) {
		super(cause);
	}

	public RowNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RowNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
