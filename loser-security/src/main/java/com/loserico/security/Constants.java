package com.loserico.security;

public class Constants {
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_MOBILE = "mobile";
	public static final String PARAM_SMS_CODE = "smscode";
	public static final String PARAM_ACCESS_TOKEN = "access-token";
	//来自什么平台，如IOS, Android, Web, pad(刷卡用)，tpad(老师端)
	public static final String PARAM_PLATFORM = "platform";
	public static final String PARAM_DEVICE_ID = "deviceId";
	public static final String PARAM_VERSION = "version";
	public static final String PLATFORM_IOS = "ios";
	public static final String PLATFORM_ANDROID = "android";
	public static final String PLATFORM_PAD = "pad";
	public static final String PLATFORM_TPAD = "tpad";
	public static final String PLATFORM_Web = "web";
	public static final String PLATFORM_WEB_GUARDIAN = "gweb";
	
	public static final String ACCOUNT_ADMIN = "admin";
	
	public static final String LOGIN_FAIL_MSG_KEY = "auth.login.failed.msg";
	public static final String AUTH_FALED_MESSAGE = "Unauthorized, token expired or not exists!";
	public static final String AUTH_FALED_USERNAME_PASSWORD = "Username.password.invalid";
	
}