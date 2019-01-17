package com.loserico.workbook.excel.exception;

/**
 * 创建Excel文件失败时抛出该异常
 * <p>
 * Copyright: Copyright (c) 2018-09-30 14:15
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class WorkbookCreationException extends RuntimeException {

	private static final long serialVersionUID = 1371909471638398673L;

	public WorkbookCreationException() {
	}

	public WorkbookCreationException(String message) {
		super(message);
	}

	public WorkbookCreationException(Throwable cause) {
		super(cause);
	}

	public WorkbookCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkbookCreationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}