package com.loserico.cache.auth;

import java.io.Serializable;

public class LoginResult<T> implements Serializable{

	private static final long serialVersionUID = -1843872171115341054L;

	private boolean success;
	
	private T lastLoginInfo;
	
	public LoginResult(){
		
	}
	
	public LoginResult(boolean success, T lastLoginInfo){
		this.success = success;
		this.lastLoginInfo = lastLoginInfo;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getLastLoginInfo() {
		return lastLoginInfo;
	}

	public void setLastLoginInfo(T lastLoginInfo) {
		this.lastLoginInfo = lastLoginInfo;
	}
	
}
