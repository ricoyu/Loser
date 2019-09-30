package com.loserico.http;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 代理工具类
 * <p>
 * Copyright: Copyright (c) 2019-03-15 10:00
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public final class ProxyUtils {

	/**
	 * 返回请求方法
	 * @param httpRequest
	 * @return
	 */
	public static String method(HttpRequest httpRequest) {
		HttpServletRequest request = (HttpServletRequest)httpRequest;
		return request.getMethod();
	}
	
	public static Object header(HttpRequest httpRequest) {
		HttpServletRequest request = (HttpServletRequest)httpRequest;
		Enumeration<String> headers = request.getHeaderNames();
		/*
		 * Header header = new Header(); while (headers.hasMoreElements()) { String headerName =
		 * (String) headers.nextElement(); request.geth }
		 */
		
		return null;
	}
}
