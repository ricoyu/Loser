package com.loserico.security;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

/**
 * Authorizer、PermissionResolver及RolePermissionResolver
 * 
 * Authorizer的职责是进行授权（访问控制），是Shiro API中授权核心的入口点，其提供了相应的角色/权限判断接口。
 * SecurityManager继承了Authorizer接口，且提供了ModularRealmAuthorizer用于多Realm时的授权匹配。
 * PermissionResolver用于解析权限字符串到Permission实例， 而RolePermissionResolver用于根据角色解析相应的权限集合
 * 
 * 我们可以通过如下ini配置更改Authorizer实现：
 * authorizer=org.apache.shiro.authz.ModularRealmAuthorizer
 * securityManager.authorizer=$authorizer
 * 
 * 对于ModularRealmAuthorizer，相应的AuthorizingSecurityManager会在初始化完成后自动将相应的realm设置进去，我们也可以通过调用其setRealms()方法进行设置。
 * 对于实现自己的authorizer可以参考ModularRealmAuthorizer实现即可，在此就不提供示例了。
 * 
 * 设置ModularRealmAuthorizer的permissionResolver，其会自动设置到相应的Realm上（其实现了PermissionResolverAware接口），如：
 * permissionResolver=org.apache.shiro.authz.permission.WildcardPermissionResolver
 * authorizer.permissionResolver=$permissionResolver
 * 
 * 设置ModularRealmAuthorizer的rolePermissionResolver，其会自动设置到相应的Realm上（其实现了RolePermissionResolverAware接口），如：
 * rolePermissionResolver=com.github.zhangkaitao.shiro.chapter3.permission.MyRolePermissionResolver
 * authorizer.rolePermissionResolver=$rolePermissionResolver
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-02-21 10:16
 * @version 1.0
 *
 */
public class AuthorizerTest {

	@Test
	public void testAuthorizer() {
		ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
		//#自定义permissionResolver
		//		WildcardPermissionResolver permissionResolver = new WildcardPermissionResolver();
		PermissionResolver permissionResolver = new BitAndWildPermissionResolver();
		authorizer.setPermissionResolver(permissionResolver);

		RolePermissionResolver rolePermissionResolver = new MyRolePermissionResolver();
		authorizer.setRolePermissionResolver(rolePermissionResolver);

		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-authorizer.ini");
		SecurityManager securityManager = factory.getInstance();
		((DefaultSecurityManager) securityManager).setAuthorizer(authorizer);

		/*
		 * 自定义realm
		 * 一定要放在securityManager.authorizer赋值之后（因为调用setRealms会将realms设置给authorizer，
		 * 并给各个Realm设置permissionResolver和rolePermissionResolver） 设置securityManager
		 * 的realms一定要放到最后，因为在调用SecurityManager.setRealms时会将realms设置给authorizer，
		 * 并为各个Realm设置permissionResolver和rolePermissionResolver。另外，
		 * 不能使用IniSecurityManagerFactory创建的IniRealm，
		 * 因为其初始化顺序的问题可能造成后续的初始化Permission造成影响。
		 */
		((DefaultSecurityManager) securityManager).setRealm(new MyRealm());

		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		subject.login(new UsernamePasswordToken("zhang", "123"));

		subject.isAuthenticated();
		subject.checkPermission("user2:create");
	}

	class BitAndWildPermissionResolver implements PermissionResolver {

		@Override
		public Permission resolvePermission(String permissionString) {
			if (permissionString.startsWith("+")) {
				return new BitPermission(permissionString);
			}
			return new WildcardPermission(permissionString);
		}
	}

	class MyRolePermissionResolver implements RolePermissionResolver {
		@Override
		public Collection<Permission> resolvePermissionsInRole(String roleString) {
			if ("role1".equals(roleString)) {
				return Arrays.asList((Permission) new WildcardPermission("menu:*"));
			}
			return null;
		}
	}

	/**
	 * 规则 +资源字符串+权限位+实例ID
	 *
	 * 以+开头 中间通过+分割
	 *
	 * 权限： 0 表示所有权限 1 新增 0001 2 修改 0010 4 删除 0100 8 查看 1000
	 *
	 * 如 +user+10 表示对资源user拥有修改/查看权限
	 *
	 * 不考虑一些异常情况
	 *
	 * <p>User: Zhang Kaitao <p>Date: 14-1-26 <p>Version: 1.0
	 */
	class BitPermission implements Permission {

		private String resourceIdentify;
		private int permissionBit;
		private String instanceId;

		public BitPermission(String permissionString) {
			String[] array = permissionString.split("\\+");

			if (array.length > 1) {
				resourceIdentify = array[1];
			}

			if (StringUtils.isEmpty(resourceIdentify)) {
				resourceIdentify = "*";
			}

			if (array.length > 2) {
				permissionBit = Integer.valueOf(array[2]);
			}

			if (array.length > 3) {
				instanceId = array[3];
			}

			if (StringUtils.isEmpty(instanceId)) {
				instanceId = "*";
			}

		}

		@Override
		public boolean implies(Permission p) {
			if (!(p instanceof BitPermission)) {
				return false;
			}
			BitPermission other = (BitPermission) p;

			if (!("*".equals(this.resourceIdentify) || this.resourceIdentify.equals(other.resourceIdentify))) {
				return false;
			}

			if (!(this.permissionBit == 0 || (this.permissionBit & other.permissionBit) != 0)) {
				return false;
			}

			if (!("*".equals(this.instanceId) || this.instanceId.equals(other.instanceId))) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "BitPermission{" +
					"resourceIdentify='" + resourceIdentify + '\'' +
					", permissionBit=" + permissionBit +
					", instanceId='" + instanceId + '\'' +
					'}';
		}
	}

	/**
	 * 此时我们继承AuthorizingRealm而不是实现Realm接口；推荐使用AuthorizingRealm，因为： AuthenticationInfo
	 * doGetAuthenticationInfo(AuthenticationToken token)：表示获取身份验证信息；
	 * AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection
	 * principals)：表示根据用户身份获取授权信息。
	 * 这种方式的好处是当只需要身份验证时只需要获取身份验证信息而不需要获取授权信息。
	 * 
	 * @author Rico Yu ricoyu520@gmail.com
	 * @since 2017-02-21 10:43
	 * @version 1.0
	 *
	 */
	class MyRealm extends AuthorizingRealm {

		@Override
		protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
			SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
			authorizationInfo.addRole("role1");
			authorizationInfo.addRole("role2");
			authorizationInfo.addObjectPermission(new BitPermission("+user1+10"));
			authorizationInfo.addObjectPermission(new WildcardPermission("user1:*"));
			authorizationInfo.addStringPermission("+user2+10");
			authorizationInfo.addStringPermission("user2:*");
			return authorizationInfo;
		}

		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String username = (String) token.getPrincipal(); //得到用户名
			String password = new String((char[]) token.getCredentials()); //得到密码
			if (!"zhang".equals(username)) {
				throw new UnknownAccountException(); //如果用户名错误
			}
			if (!"123".equals(password)) {
				throw new IncorrectCredentialsException(); //如果密码错误
			}
			//如果身份认证验证成功，返回一个AuthenticationInfo实现；
			return new SimpleAuthenticationInfo(username, password, getName());
		}
	}

}
