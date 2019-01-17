package com.loserico.security.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-02-26 20:45
 * @version 1.0
 *
 */
public class StatelessToken implements AuthenticationToken {

	private static final long serialVersionUID = 1L;
	private String accessToken;
	
	public StatelessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public Object getPrincipal() {
		return accessToken;
	}

	@Override
	public Object getCredentials() {
		return accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}