package com.loserico.security;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Permission 字符串通配符权限
 * 
 * 规则;"资源标识符;操作;对象实例ID"  即对哪个资源的哪个实例可以进行什么操作。
 * 默认支持通配符权限字符串，
 * 	":"表示资源/操作/实例的分割
 *  ","表示操作的分割;
 *  "*"表示任意资源/操作/实例。
 * 
 * 单个资源单个权限	subject().checkPermissions("system:user:update"); 
 * 单个资源多个权限	subject().checkPermissions("system:user:update", "system:user:delete"); 
 * 					subject().checkPermissions("system:user:update,delete"); 
 * 
 * role51="system:user:create,update,delete,view"  
 * 用户拥有资源"system:user"的"create"、"update"、"delete"和"view"所有权限。如上可以简写成;
 * role52=system:user:* 
 * 也可以简写为（推荐上边的写法）;
 * role53=system:user  
 * 然后通过如下代码判断
 * subject().checkPermissions("system:user:*");
 * subject().checkPermissions("system:user");  
 * 通过"system:user:*"验证"system:user:create,delete,update:view"可以，但是反过来是不成立的
 * 
 * 所有资源全部权限
 * role61=*:view 
 * 然后通过如下代码判断
 * subject().checkPermissions("user:view"); 
 * 用户拥有所有资源的"view"所有权限。假设判断的权限是"system:user:view"，那么需要"role5=*:*:view"这样写才行。
 * 
 * 实例级别的权限
 * 
 * 单个实例单个权限
 * role71=user:view:1  
 * 对资源user的1实例拥有view权限。然后通过如下代码判断 
 * subject().checkPermissions("user:view:1");  
 * 
 * 单个实例多个权限
 * role72="user:update,delete:1" 
 * 对资源user的1实例拥有update、delete权限。然后通过如下代码判断
 * subject().checkPermissions("user:delete,update:1");  
 * subject().checkPermissions("user:update:1", "user:delete:1");
 * 
 * 单个实例所有权限
 * role73=user:*:1
 * 对资源user的1实例拥有所有权限
 * subject().checkPermissions("user:update:1", "user:delete:1", "user:view:1"); 
 * 
 * 所有实例单个权限
 * role74=user:auth:*
 * 对资源user的1实例拥有所有权限。
 * subject().checkPermissions("user:auth:1", "user:auth:2");  
 * 
 * 所有实例所有权限
 * role75=user:*:*
 * 对资源user的1实例拥有所有权限。然后通过如下代码判断     
 * subject().checkPermissions("user:view:1", "user:auth:2"); 
 * 
 * Shiro对权限字符串缺失部分的处理
 * 如"user:view"等价于"user:view:*";而"organization"等价于"organization:*"或者"organization:*:*"。
 * 可以这么理解，这种方式实现了前缀匹配。
 * 另外如"user:*"可以匹配如"user:delete"、"user:delete"可以匹配如"user:delete:1"、"user:*:1"可以匹配如"user:view:1"、"user"可以匹配"user:view"或"user:view:1"等。
 * 即*可以匹配所有，不加*可以进行前缀匹配;但是如"*:view"不能匹配"system:user:view"，需要使用"*:*:view"，即后缀匹配必须指定前缀（多个冒号就需要多个*来匹配）。
 * 
 * WildcardPermission
 * 如下两种方式是等价的：  
 * subject().checkPermission("menu:view:1");  
 * subject().checkPermission(new WildcardPermission("menu:view:1"));
 * 因此没什么必要的话使用字符串更方便。
 * 
 * 授权流程
 * 
 * 流程如下：
 * 1、首先调用Subject.isPermitted/hasRole接口，其会委托给SecurityManager，而SecurityManager接着会委托给Authorizer
 * 2、Authorizer是真正的授权者，如果我们调用如isPermitted("user:view")，其首先会通过PermissionResolver把字符串转换成相应的Permission实例；
 * 3、在进行授权之前，其会调用相应的Realm获取Subject相应的角色/权限用于匹配传入的角色/权限；
 * 4、Authorizer会判断Realm的角色/权限是否和传入的匹配，如果有多个Realm，会委托给ModularRealmAuthorizer进行循环判断，
 * 	如果匹配如isPermitted/hasRole会返回true，否则返回false表示授权失败。
 * 
 * ModularRealmAuthorizer进行多Realm匹配流程：
 * 1、首先检查相应的Realm是否实现了实现了Authorizer；
 * 2、如果实现了Authorizer，那么接着调用其相应的isPermitted/hasRole接口进行匹配；
 * 3、如果有一个Realm匹配那么将返回true，否则返回false。
 * 
 * 如果Realm进行授权的话，应该继承AuthorizingRealm，其流程是：
 * 1.1、如果调用hasRole*，则直接获取AuthorizationInfo.getRoles()与传入的角色比较即可；
 * 1.2、首先如果调用如isPermitted("user:view")，首先通过PermissionResolver将权限字符串转换成相应的Permission实例，
 * 		默认使用WildcardPermissionResolver，即转换为通配符的WildcardPermission;
 * 2、通过AuthorizationInfo.getObjectPermissions()得到Permission实例集合;
 * 		通过AuthorizationInfo. getStringPermissions()得到字符串集合并通过PermissionResolver解析为Permission实例;
 * 		然后获取用户的角色, 并通过RolePermissionResolver解析角色对应的权限集合（默认没有实现，可以自己提供）;
 * 3、接着调用Permission.implies(Permission p)逐个与传入的权限比较，如果有匹配的则返回true，否则false。
 * 
 * @on
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-02-21 09:58
 * @version 1.0
 *
 */
public class AuthorizationTest {

	/*
	 * @of
	 * 基于角色的访问控制（隐式角色）
	 * 
	 * 1、在ini配置文件配置用户拥有的角色（shiro-role.ini）
	 * [users] 
	 * zhang=123,role1,role2
	 * wang=123,role1
	 * 规则即: "用户名=密码,角色1，角色2"，
	 * 如果需要在应用中判断用户是否有相应角色，就需要在相应的Realm中返回角色信息，也就是说Shiro不负责维护用户-角色信息，需要应用提供，
	 * Shiro只是提供相应的接口方便验证，后续会介绍如何动态的获取用户角色。
	 * 
	 * 到此基于角色的访问控制（即隐式角色）就完成了，这种方式的缺点就是如果很多地方进行了角色判断，
	 * 但是有一天不需要了那么就需要修改相应代码把所有相关的地方进行删除;这就是粗粒度造成的问题。
	 * @on
	 */
	@Test
	public void testAuthorizationByRole() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-role.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		assertTrue(subject.isAuthenticated());//断言用户已经登录
		//判断拥有角色: role1
		assertTrue(subject.hasRole("role1"));
		//判断拥有角色: role1 and role2
		assertTrue(subject.hasAllRoles(newArrayList("role1", "role2")));

		//判断拥有角色: role1 and role2 and !role3
		boolean[] results = subject.hasRoles(newArrayList("role1", "role2", "role3"));
		Assert.assertEquals(true, results[0]);
		Assert.assertEquals(true, results[1]);
		Assert.assertEquals(false, results[2]);

		//断言拥有角色: role1  
		subject.checkRole("role1");
		try {
			subject.checkRole("role3");
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		}
		//6、退出 
		subject.logout();

	}

	/*
	 * @of
	 * 基于资源的访问控制（显示角色）
	 * 
	 * 在ini配置文件配置用户拥有的角色及角色-权限关系（shiro-permission.ini） 
	 * [users] 
	 * zhang=123,role1,role2
	 * wang=123,role1 
	 * [roles] 
	 * role1=user:create,user:update
	 * role2=user:create,user:delete
	 * 
	 * 规则: 
	 * "用户名=密码，角色1，角色2"
	 * "角色=权限1，权限2"
	 * 
	 * 即首先根据用户名找到角色，然后根据角色再找到权限;即角色是权限集合;
	 * Shiro同样不进行权限的维护，需要我们通过Realm返回相应的权限信息。只需要维护"用户——角色"之间的关系即可。
	 * 
	 * Shiro提供了isPermitted和isPermittedAll用于判断用户是否拥有某个权限或所有权限，
	 * 也没有提供如isPermittedAny用于判断拥有某一个权限的接口。
	 * 
	 * 到此基于资源的访问控制（显示角色）就完成了，也可以叫基于权限的访问控制，这种方式的一般规则是"资源标识符: 操作"，即是资源级别的粒度;
	 * 这种方式的好处就是如果要修改基本都是一个资源级别的修改，不会对其他模块代码产生影响，粒度小。
	 * 但是实现起来可能稍微复杂点，需要维护 "用户——角色，角色——权限(资源:操作)" 之间的关系。
	 * @on
	 */
	@Test
	//	@Test(expected = AuthorizationException.class)
	public void testAuthorizationByPermission() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-permission.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		//		UsernamePasswordToken token = new UsernamePasswordToken("wang", "123");
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		//断言拥有权限;user:create
		System.out.println("user:*:view " + subject.isPermitted("user:*:view"));
		System.out.println("user:create " + subject.isPermitted("user:create"));
		System.out.println("user:update " + subject.isPermitted("user:update"));
		System.out.println("user:delete " + subject.isPermitted("user:delete"));
		System.out.println("user " + subject.isPermitted("user"));
		/*
		 * role4=user:*:view role1=user:create role3=user:update role2=user:delete
		 * role5=user
		 */
		//		System.out.println(subject.isPermitted("user:update "));
		//		System.out.println(subject.isPermitted("bbcg:count"));;
		//		System.out.println(subject.isPermitted("bbcg:count:unverified"));;
		//		System.out.println(subject.isPermitted("bbcg:count:unverified:classes"));
		//		subject.checkPermission("bbcg:count");
		//		subject.checkPermission("bbcg:count:unverified");
		//		subject.checkPermission("bbcg:count:unverified:classes");
		//		subject.checkPermission("user:create");
		//		subject.checkPermission("user:delete");
		//断言拥有权限;user:delete and user:update 
		//		subject.checkPermissions("user:create", "user:update");
		//断言拥有权限;user:view 失败抛出异常  
		//		subject.checkPermission("user:view");
	}
}
