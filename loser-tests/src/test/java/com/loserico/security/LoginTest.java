package com.loserico.security;

import static org.junit.Assert.assertTrue;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

public class LoginTest {

	/*
	 * @of
	 * 2.1、首先通过new IniSecurityManagerFactory并指定一个ini配置文件来创建一个SecurityManager工厂；
	 * 2.2、接着获取SecurityManager并绑定到SecurityUtils，这是一个全局设置，设置一次即可；
	 * 2.3、通过SecurityUtils得到Subject，其会自动绑定到当前线程；如果在web环境在请求结束时需要解除绑定；然后获取身份验证的Token，
	 * 		如用户名/密码； 2.4、调用subject.login方法进行登录，其会自动委托给SecurityManager.login方法进行登录；
	 * 2.5、如果身份验证失败请捕获AuthenticationException或其子类，常见的如：
	 * 		DisabledAccountException（禁用的帐号）
	 * 		LockedAccountException（锁定的帐号）
	 * 		UnknownAccountException（错误的帐号）
	 * 		ExcessiveAttemptsException（登录失败次数过多）
	 * 		IncorrectCredentialsException （错误的凭证）
	 * 		ExpiredCredentialsException（过期的凭证）等，
	 * 
	 * 具体请查看其继承关系；对于页面的错误消息展示，最好使用如"用户名/密码错误"而不是"用户名错误"/"密码错误"，防止一些恶意用户非法扫描帐号库；
	 * 2.6、最后可以调用subject.logout退出，其会自动委托给SecurityManager.logout方法退出。
	 * 
	 * 身份认证流程
	 * 
	 * 流程如下：
	 * 1、首先调用Subject.login(token)进行登录，其会自动委托给Security Manager，调用之前必须通过SecurityUtils. setSecurityManager()设置；
	 * 2、SecurityManager负责真正的身份验证逻辑；它会委托给Authenticator进行身份验证；
	 * 3、Authenticator才是真正的身份验证者，Shiro API中核心的身份认证入口点，此处可以自定义插入自己的实现；
	 * 4、Authenticator可能会委托给相应的AuthenticationStrategy进行多Realm身份验证，默认ModularRealmAuthenticator会调用AuthenticationStrategy进行多Realm身份验证；
	 * 5、Authenticator会把相应的token传入Realm，从Realm获取身份验证信息，如果没有返回/抛出异常表示身份验证失败了。此处可以配置多个Realm，将按照相应的顺序及策略进行访问。
	 * @on
	 */
	@Test
	public void testHelloWorld() {
		//1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager  
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		//2、得到SecurityManager实例 并绑定给SecurityUtils 
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		//3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证） 
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		//4、登录，即身份验证  
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//5、身份验证失败
		}

		assertTrue(subject.isAuthenticated());//断言用户已经登录
		//6、退出 
		subject.logout();
	}
}
