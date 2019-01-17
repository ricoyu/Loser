package com.loserico.search.exeption;

/**
 * 执行MultiGet操作时有Failure的情况
 * <p>
 * Copyright: Copyright (c) 2018-08-24 15:27
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class MultiGetRequestException extends RuntimeException {

	private static final long serialVersionUID = -397453679979156501L;

	public MultiGetRequestException() {
		super();
	}

	public MultiGetRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MultiGetRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public MultiGetRequestException(String message) {
		super(message);
	}

	public MultiGetRequestException(Throwable cause) {
		super(cause);
	}

}
