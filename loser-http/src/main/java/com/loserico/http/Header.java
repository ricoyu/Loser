package com.loserico.http;

/**
 * HTTP 请求头
 * <p>
 * Copyright: Copyright (c) 2019-03-15 10:04
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class Header {

	private volatile boolean intHeader;

	private volatile boolean dateHeader;

	private volatile boolean stringHeader;

	private String name;

	private String stringValue;

	private int intValue;

	private long dateValue;

	/**
	 * 根据Header值的类型分成三类
	 */
	enum HeaderType {
		INT,
		DATE,
		STRING;
	}

	public Header(String name, String value) {
		this.name = name;
		this.stringValue = value;
		this.stringHeader = true;
	}

	public Header(String name, int value) {
		this.name = name;
		this.intValue = value;
		this.intHeader = true;
	}

	public Header(String name, long value) {
		this.name = name;
		this.dateValue = value;
		this.dateHeader = true;
	}
	
	/**
	 * 返回HTTP Header的类型
	 * @return HeaderType
	 */
	public HeaderType type() {
		if (intHeader) {
			return HeaderType.INT;
		}
		if (dateHeader) {
			return HeaderType.DATE;
		}
		return HeaderType.STRING;
	}
	
	public String name() {
		return this.name;
	}
	
	public Object value() {
		if (intHeader) {
			return this.intValue;
		}
		if (dateHeader) {
			return this.dateValue;
		}
		return stringValue;
	}
}
