package com.loserico.search.exeption;

/**
 * POJO上Indexed注解未配置或者配置不当时抛出
 * <p>
 * Copyright: Copyright (c) 2018-08-24 10:33
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class IndexedConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -1689997896379157432L;

	public IndexedConfigurationException() {
		super();
	}

	public IndexedConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IndexedConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexedConfigurationException(String message) {
		super(message);
	}

	public IndexedConfigurationException(Throwable cause) {
		super(cause);
	}

}
