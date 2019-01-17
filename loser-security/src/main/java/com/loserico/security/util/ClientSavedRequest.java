package com.loserico.security.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.util.SavedRequest;

public class ClientSavedRequest extends SavedRequest {
	private static final long serialVersionUID = 245752842145634533L;
	private String contextPath;
	private String backUrl;//跟在url后面的一个请求参数,?backUrl=xxx
	private String scheme;
	private String domain;
	private int port;

	public ClientSavedRequest(HttpServletRequest request, String backUrl) {
		super(request);
		this.scheme = request.getScheme();
		this.domain = request.getServerName();
		this.port = request.getServerPort();
		this.backUrl = backUrl;
		this.contextPath = request.getContextPath();
	}

	public String getScheme() {
		return scheme;
	}

	public String getDomain() {
		return domain;
	}

	public int getPort() {
		return port;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public String getRequestUrl() {
		String requestURI = getRequestURI();
		//如果从外部传入了successUrl（登录成功之后重定向的地址），且以http://或https://开头那么直接返回（相应的拦截器直接重定向到它即可）
		if (backUrl != null) {
			if (backUrl.toLowerCase().startsWith("http://") || backUrl.toLowerCase().startsWith("https://")) {
				return backUrl;
			} else if (!backUrl.startsWith(contextPath)) {//如果successUrl有值但没有上下文，拼上上下文
				requestURI = contextPath + backUrl;
			} else {
				requestURI = backUrl;
			}
		}

		//如果successUrl没值，那么requestUrl就是当前请求的地址
		StringBuilder requestUrl = new StringBuilder(scheme);
		requestUrl.append("://");
		requestUrl.append(domain);

		if ("http".equalsIgnoreCase(scheme) && port != 80) {
			requestUrl.append(":").append(String.valueOf(port));
		} else if ("https".equalsIgnoreCase(scheme) && port != 443) {
			requestUrl.append(":").append(String.valueOf(port));
		}

		requestUrl.append(requestURI);

		if (backUrl == null && getQueryString() != null) {
			requestUrl.append("?").append(getQueryString());
		}
		return requestUrl.toString();
	}
}