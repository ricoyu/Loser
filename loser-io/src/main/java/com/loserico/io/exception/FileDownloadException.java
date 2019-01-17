package com.loserico.io.exception;

import java.nio.file.Path;

public class FileDownloadException extends RuntimeException {

	private static final long serialVersionUID = 2376033588859579228L;
	private Path path;

	public FileDownloadException() {
		super();
	}

	public FileDownloadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileDownloadException(String message) {
		super(message);
	}

	public FileDownloadException(Throwable cause) {
		super(cause);
	}

	public FileDownloadException(Path path, Throwable cause) {
		super(cause);
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
