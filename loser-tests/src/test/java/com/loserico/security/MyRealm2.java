package com.loserico.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class MyRealm2 extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	/*
	 * 此处就是把com.loserico.security.HashedCredentialsMatcherTest.testEncryptPassword()
	 * 中生成的相应数据组装为SimpleAuthenticationInfo，
	 * 通过SimpleAuthenticationInfo的credentialsSalt设置盐，HashedCredentialsMatcher会自动识别这个盐。
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = "rico"; //用户名及salt1  
		String password = "c0a3956661cbe810ac3e7f3e2d709523"; //加密后的密码  
		String salt2 = "a1de8a104c548df25afcc0d89627080d";
		SaltedAuthenticationInfo saltedAuthenticationInfo = new SimpleAuthenticationInfo(username, password,
				ByteSource.Util.bytes(username + salt2), getAuthenticationCacheName());
		return saltedAuthenticationInfo;
	}

}
