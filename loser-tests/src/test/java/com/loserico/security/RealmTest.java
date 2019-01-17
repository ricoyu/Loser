package com.loserico.security;

import static org.junit.Assert.assertTrue;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

/**
 * Realm: 域
 * 
 * Shiro从从Realm获取安全数据（如用户、角色、权限），就是说SecurityManager要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法;
 * 也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作;可以把Realm看成DataSource，即安全数据源。
 * 如我们之前的ini配置方式将使用org.apache.shiro.realm.text.IniRealm。
 * 
 * 以后一般继承AuthorizingRealm（授权）即可；其继承了AuthenticatingRealm（即身份验证），而且也间接继承了CachingRealm（带有缓存实现）。
 * 其中主要默认实现如下：
 * org.apache.shiro.realm.text.IniRealm：
 * 		[users]部分指定用户名/密码及其角色；[roles]部分指定角色即权限信息；
 * org.apache.shiro.realm.text.PropertiesRealm：
 * 		user.username=password,role1,role2指定用户名/密码及其角色；
 * 		role.role1=permission1,permission2指定角色及权限信息；
 * org.apache.shiro.realm.jdbc.JdbcRealm：通过sql查询相应的信息，
 * 如
 * "select password from users where username = ?" 获取用户密码，
 * "select password, password_salt from users where username = ?"获取用户密码及盐；
 * "select role_name from user_roles where username = ?"获取用户角色；
 * "select permission from roles_permissions where role_name = ?"获取角色对应的权限信息；也可以调用相应的api进行自定义sql；
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-02-21 08:33
 * @version 1.0
 *
 */
public class RealmTest {

	@Test
	public void testMyRealm() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		((DefaultSecurityManager) securityManager).setRealm(new MyRealm1());
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		assertTrue(subject.isAuthenticated());//断言用户已经登录
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
}
