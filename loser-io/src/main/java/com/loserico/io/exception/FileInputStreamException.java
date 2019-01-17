package com.loserico.io.exception;

/**
 *  读取文件到InputStream出错抛出次异常
 * <p>
 * Copyright: Copyright (c) 2018-06-13 14:47
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class FileInputStreamException extends RuntimeException {

	private static final long serialVersionUID = 6445182002211733856L;

	public FileInputStreamException() {
		super();
	}

	public FileInputStreamException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileInputStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileInputStreamException(String message) {
		super(message);
	}

	public FileInputStreamException(Throwable cause) {
		super(cause);
	}

}
