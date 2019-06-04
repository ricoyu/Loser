package com.loserico.cache.exception;

/**
 * 没有建立任何Jedission Client时, 会抛出改异常
 * <p>
 * Copyright: Copyright (c) 2019-06-03 10:17
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class NoJedissionClientException extends RuntimeException {

	private static final long serialVersionUID = 171028170380233162L;

	public NoJedissionClientException() {
	}

	public NoJedissionClientException(String message) {
		super(message);
	}

	public NoJedissionClientException(Throwable cause) {
		super(cause);
	}

	public NoJedissionClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoJedissionClientException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
