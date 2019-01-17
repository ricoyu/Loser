package com.loserico.workbook.exception;

/**
 * 给定的Workbook格式不能被识别时抛出
 * <p>
 * Copyright: Copyright (c) 2018-12-21 17:37
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class UnrecognizedWorkbookException extends RuntimeException {

	private static final long serialVersionUID = -7019678665057695863L;

	public UnrecognizedWorkbookException() {
		super();
	}

	public UnrecognizedWorkbookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnrecognizedWorkbookException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnrecognizedWorkbookException(String message) {
		super(message);
	}

	public UnrecognizedWorkbookException(Throwable cause) {
		super(cause);
	}

}
