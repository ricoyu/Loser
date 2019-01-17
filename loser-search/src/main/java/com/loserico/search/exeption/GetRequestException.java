package com.loserico.search.exeption;

/**
 * 获取文档请求出错
 * <p>
 * Copyright: Copyright (c) 2018-08-21 10:09
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class GetRequestException extends RuntimeException {

	private static final long serialVersionUID = 4597000586211488660L;

	public GetRequestException() {
		super();
	}

	public GetRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GetRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public GetRequestException(String message) {
		super(message);
	}

	public GetRequestException(Throwable cause) {
		super(cause);
	}

}
