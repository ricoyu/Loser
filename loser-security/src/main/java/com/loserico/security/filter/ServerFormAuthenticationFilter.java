package com.loserico.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * 如果是从其他应用中重定向过来的，首先检查Session中是否有“authc.fallbackUrl”属性，
 * 如果有就认为它是默认的重定向地址；否则使用Server自己的successUrl作为登录成功后重定向到的地址。
 * @author Loser
 * @since May 17, 2016
 * @version 
 *
 */
public class ServerFormAuthenticationFilter extends FormAuthenticationFilter {

	protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
		String fallbackUrl = (String) getSubject(request, response).getSession().getAttribute("authc.fallbackUrl");
		if (StringUtils.isEmpty(fallbackUrl)) {
			fallbackUrl = getSuccessUrl();
		}
		WebUtils.redirectToSavedRequest(request, response, fallbackUrl);
	}
}