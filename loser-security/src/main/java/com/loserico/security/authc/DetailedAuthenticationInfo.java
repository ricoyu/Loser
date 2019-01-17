package com.loserico.security.authc;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class DetailedAuthenticationInfo extends SimpleAuthenticationInfo {
	
	private static final long serialVersionUID = 5694002318112815046L;

	//姓
	private String surname;
	
	//名
	private String givenName;
	
	//手机
	private String mobile;
	
	private String email;
	
	//性别
	private String gender;

	public DetailedAuthenticationInfo() {
		super();
	}

	public DetailedAuthenticationInfo(Object principal, Object hashedCredentials, ByteSource credentialsSalt,
			String realmName) {
		super(principal, hashedCredentials, credentialsSalt, realmName);
	}

	public DetailedAuthenticationInfo(Object principal, Object credentials, String realmName) {
		super(principal, credentials, realmName);
	}

	public DetailedAuthenticationInfo(PrincipalCollection principals, Object hashedCredentials,
			ByteSource credentialsSalt) {
		super(principals, hashedCredentials, credentialsSalt);
	}

	public DetailedAuthenticationInfo(PrincipalCollection principals, Object credentials) {
		super(principals, credentials);
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
