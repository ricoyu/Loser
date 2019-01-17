package com.loserico.commons.exception;

/**
 * 
 * <p>
 * Copyright: Copyright (c) 2018-06-21 13:49
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class BeanCloneException extends RuntimeException {

	private static final long serialVersionUID = -2779501661434138673L;

	public BeanCloneException() {
		super();
	}

	public BeanCloneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeanCloneException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanCloneException(String message) {
		super(message);
	}

	public BeanCloneException(Throwable cause) {
		super(cause);
	}

}
