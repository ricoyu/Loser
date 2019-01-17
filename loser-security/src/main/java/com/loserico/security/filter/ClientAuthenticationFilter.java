package com.loserico.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import com.loserico.security.util.ClientSavedRequest;

/**
 * ClientAuthenticationFilter是用于实现身份认证的拦截器（authc），当用户没有身份认证时；
 * 1、首先得到请求参数backUrl，即登录成功重定向到的地址；
 * 2、然后保存保存请求到会话，并重定向到登录地址（server模块）；
 * 3、登录成功后，返回地址按照如下顺序获取：backUrl、保存的当前请求地址、defaultBackUrl（即设置的successUrl）；
 * @author Loser
 * @since May 17, 2016
 * @version 
 *
 */
public class ClientAuthenticationFilter extends AuthenticationFilter {
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);
		return subject.isAuthenticated();
	}

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		String backUrl = request.getParameter("backUrl");
		saveRequest(request, backUrl, getDefaultBackUrl(WebUtils.toHttp(request)));
		return false;
	}

	protected void saveRequest(ServletRequest request, String backUrl, String fallbackUrl) {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		session.setAttribute("authc.fallbackUrl", fallbackUrl);
		SavedRequest savedRequest = new ClientSavedRequest(httpRequest, backUrl);
		session.setAttribute(WebUtils.SAVED_REQUEST_KEY, savedRequest);
	}

	private String getDefaultBackUrl(HttpServletRequest request) {
		String scheme = request.getScheme();
		String domain = request.getServerName();
		int port = request.getServerPort();
		String contextPath = request.getContextPath();
		StringBuilder backUrl = new StringBuilder(scheme);
		backUrl.append("://");
		backUrl.append(domain);
		if ("http".equalsIgnoreCase(scheme) && port != 80) {
			backUrl.append(":").append(String.valueOf(port));
		} else if ("https".equalsIgnoreCase(scheme) && port != 443) {
			backUrl.append(":").append(String.valueOf(port));
		}
		backUrl.append(contextPath);
		backUrl.append(getSuccessUrl());
		return backUrl.toString();
	}
}