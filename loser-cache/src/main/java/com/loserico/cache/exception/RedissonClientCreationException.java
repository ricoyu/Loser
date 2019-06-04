package com.loserico.cache.exception;

/**
 * 创建RedissonClient时抛的异常
 * <p>
 * Copyright: Copyright (c) 2019-06-03 18:13
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedissonClientCreationException extends RuntimeException {

	private static final long serialVersionUID = -5543000464563412116L;

	public RedissonClientCreationException() {
	}

	public RedissonClientCreationException(String message) {
		super(message);
	}

	public RedissonClientCreationException(Throwable cause) {
		super(cause);
	}

	public RedissonClientCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedissonClientCreationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
