package com.loserico.security;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertTrue;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

/**
 * @of
 * Authenticator的职责是验证用户帐号,是Shiro API中身份验证核心的入口点:
 * 		public AuthenticationInfo authenticate(AuthenticationToken authenticationToken) throws AuthenticationException;
 * 
 * SecurityManager接口继承了Authenticator,另外还有一个ModularRealmAuthenticator实现,其委托给多个Realm进行验证,验证规则通过AuthenticationStrategy接口指定,
 * 默认提供的实现:
 * FirstSuccessfulStrategy		只要有一个Realm验证成功即可,只返回第一个Realm身份验证成功的认证信息,其他的忽略;
 * AtLeastOneSuccessfulStrategy	只要有一个Realm验证成功即可,和FirstSuccessfulStrategy不同,返回所有Realm身份验证成功的认证信息;
 * AllSuccessfulStrategy		所有Realm验证成功才算成功,且返回所有Realm身份验证成功的认证信息,如果有一个失败就失败了。
 * 
 * ModularRealmAuthenticator默认使用AtLeastOneSuccessfulStrategy策略。
 * 
 * 假设我们有三个realm:
 * myRealm1:用户名/密码为zhang/123时成功,且返回身份/凭据为zhang/123;
 * myRealm2:用户名/密码为wang/123时成功,且返回身份/凭据为wang/123; 
 * myRealm3:用户名/密码为zhang/123时成功,且返回身份/凭据为zhang@163.com/123,和myRealm1不同的是返回时的身份变了;
 * 
 * 对于AtLeastOneSuccessfulStrategy和FirstSuccessfulStrategy的区别，唯一不同点一个是返回所有验证成功的Realm的认证信息；另一个是只返回第一个验证成功的Realm的认证信息。
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-02-21 08:51
 * @version 1.0
 *
 */
public class AuthenticatorTest {

	@Test
	public void testAuthenticator() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		Authenticator authenticator = new ModularRealmAuthenticator();
		((ModularRealmAuthenticator) authenticator)
				.setRealms(newArrayList(new MyRealm1(), new MyRealm2(), new MyRealm3()));
		((ModularRealmAuthenticator) authenticator).setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		//				((ModularRealmAuthenticator) authenticator).setAuthenticationStrategy(new FirstSuccessfulStrategy());
		//				((ModularRealmAuthenticator) authenticator).setAuthenticationStrategy(new AllSuccessfulStrategy());
		((DefaultSecurityManager) securityManager).setAuthenticator(authenticator);
		/*
		 * ((DefaultSecurityManager) securityManager).getRealms()
		 * .addAll(newArrayList(new MyRealm1(), new MyRealm2(), new MyRealm3()));
		 */
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		assertTrue(subject.isAuthenticated());//断言用户已经登录
		PrincipalCollection principalCollection = subject.getPrincipals();
		principalCollection.asList().forEach((obj) -> {
			System.out.println(obj);
		});
		//6、退出 
		subject.logout();
	}

	class MyRealm1 implements Realm {

		@Override
		public String getName() {
			return "MyRealm1";
		}

		@Override
		public boolean supports(AuthenticationToken token) {
			//仅支持UsernamePasswordToken类型的Token  
			return token instanceof UsernamePasswordToken;
		}

		@Override
		public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String username = (String) token.getPrincipal();
			String password = new String((char[]) token.getCredentials());
			if (!"zhang".equals(username)) {
				throw new UnknownAccountException();//如果用户名错误 
			}
			if (!"123".equals(password)) {
				throw new IncorrectCredentialsException();//如果密码错误
			}
			//如果身份认证验证成功，返回一个AuthenticationInfo实现；
			return new SimpleAuthenticationInfo(username, password, getName());
		}

	}

	class MyRealm2 implements Realm {

		@Override
		public String getName() {
			return "MyRealm2";
		}

		@Override
		public boolean supports(AuthenticationToken token) {
			//仅支持UsernamePasswordToken类型的Token  
			return token instanceof UsernamePasswordToken;
		}

		@Override
		public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String username = (String) token.getPrincipal();
			String password = new String((char[]) token.getCredentials());
			if (!"wang".equals(username)) {
				throw new UnknownAccountException();//如果用户名错误 
			}
			if (!"123".equals(password)) {
				throw new IncorrectCredentialsException();//如果密码错误
			}
			//如果身份认证验证成功，返回一个AuthenticationInfo实现；
			return new SimpleAuthenticationInfo(username, password, getName());
		}

	}

	class MyRealm3 implements Realm {

		@Override
		public String getName() {
			return "MyRealm3";
		}

		@Override
		public boolean supports(AuthenticationToken token) {
			//仅支持UsernamePasswordToken类型的Token  
			return token instanceof UsernamePasswordToken;
		}

		@Override
		public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String username = (String) token.getPrincipal();
			String password = new String((char[]) token.getCredentials());
			if (!"zhang".equals(username)) {
				throw new UnknownAccountException();//如果用户名错误 
			}
			if (!"123".equals(password)) {
				throw new IncorrectCredentialsException();//如果密码错误
			}
			//如果身份认证验证成功，返回一个AuthenticationInfo实现；
			return new SimpleAuthenticationInfo("zhang@163.com", password, getName());
		}

	}
}
