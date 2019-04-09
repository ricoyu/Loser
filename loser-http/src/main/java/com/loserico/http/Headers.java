package com.loserico.http;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
public class Headers {

	private static final String[] DATE_HEADERS = new String[] { "Date", "If-Modified-Since", "If-Unmodified-Since" };
	private static final String[] INT_HEADERS = new String[] { "Max-Forwards" };

	private final Map<String, Object> headers;

	public Headers() {
		this.headers = new HashMap<>();
	}
	
	public void requestHeaders(ServletRequest servletRequest) {
		
	}

	public Headers(Map<String, Object> headers) {
		this.headers = headers;
	}

	public void put(String header, Object value) {
		this.headers.put(header, value);
	}

	/**
	 * 检查HTTP Header值是否是日期类型的
	 * 
	 * @param header
	 * @return boolean
	 */
	public static boolean isDateHeader(String header) {
		for (int i = 0; i < DATE_HEADERS.length; i++) {
			if (DATE_HEADERS[i].equalsIgnoreCase(header)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查HTTP Header值是否是INT型的
	 * 
	 * @param header
	 * @return boolean
	 */
	public static boolean isIntHeader(String header) {
		for (int i = 0; i < INT_HEADERS.length; i++) {
			if (INT_HEADERS[i].equalsIgnoreCase(header)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取INT型HEADER的值
	 * 
	 * @param request
	 * @param header
	 * @return int
	 */
	public static int intValue(HttpServletRequest request, String header) {
		return request.getIntHeader(header);
	}

	/**
	 * 获取日期类型的HEADER值, 返回的是epoll miliseconds
	 * 
	 * @param request
	 * @param header
	 * @return long
	 */
	public static long dateHeader(HttpServletRequest request, String header) {
		return request.getDateHeader(header);
	}
}
