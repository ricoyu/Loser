package com.loserico.search.exeption;

/**
 * Exists 请求异常
 * <p>
 * Copyright: Copyright (c) 2018-08-21 22:21
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ExistsRequestException extends RuntimeException {

	public ExistsRequestException() {
		super();
	}

	public ExistsRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExistsRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExistsRequestException(String message) {
		super(message);
	}

	public ExistsRequestException(Throwable cause) {
		super(cause);
	}

}
