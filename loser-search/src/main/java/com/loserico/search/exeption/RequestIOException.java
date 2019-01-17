package com.loserico.search.exeption;

/**
 * 封装了请求时跑出的IOException
 * <p>
 * Copyright: Copyright (c) 2018-08-21 22:31
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RequestIOException extends RuntimeException {

	private static final long serialVersionUID = 2554996032056365209L;

	public RequestIOException() {
		super();
	}

	public RequestIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RequestIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestIOException(String message) {
		super(message);
	}

	public RequestIOException(Throwable cause) {
		super(cause);
	}

}
