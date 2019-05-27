package com.loserico.concurrent.exception;
/**
 * 并发操作异常类
 * <p>
 * Copyright: Copyright (c) 2018-11-15 15:31
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ConcurrentOperationException extends RuntimeException {

	private static final long serialVersionUID = 5008555376612220264L;

	public ConcurrentOperationException() {
	}

	public ConcurrentOperationException(String message) {
		super(message);
	}

	public ConcurrentOperationException(Throwable cause) {
		super(cause);
	}

	public ConcurrentOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConcurrentOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}