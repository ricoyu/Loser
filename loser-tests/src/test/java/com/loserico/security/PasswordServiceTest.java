package com.loserico.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.crypto.hash.format.DefaultHashFormatFactory;
import org.apache.shiro.crypto.hash.format.HashFormat;
import org.apache.shiro.crypto.hash.format.HashFormatFactory;
import org.apache.shiro.crypto.hash.format.Shiro1CryptFormat;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @of
 * Shiro提供了PasswordService及CredentialsMatcher用于提供加密密码及验证密码服务。
 * public interface PasswordService {
 * 		//输入明文密码得到密文密码
 * 		String encryptPassword(Object plaintextPassword) throws IllegalArgumentException;
 * }  
 * 
 * public interface CredentialsMatcher {
 * 		//匹配用户输入的token的凭证（未加密）与系统提供的凭证（已加密）
 * 		boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info);
 * }
 * 
 * Shiro默认提供了
 * PasswordService的实现		DefaultPasswordService；
 * CredentialsMatcher的实现	PasswordMatcher及HashedCredentialsMatcher（更强大）。
 * 
 * @on
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-02-19 20:22
 * @version 1.0
 *
 */
public class PasswordServiceTest {

	/*
	 * @of
	 * DefaultPasswordService配合PasswordMatcher实现简单的密码加密与验证服务
	 * 
	 * passwordService使用DefaultPasswordService，如果有必要也可以自定义；
	 * hashService定义散列密码使用的HashService，默认使用DefaultHashService（默认SHA-256算法）；
	 * hashFormat用于对散列出的值进行格式化，默认使用Shiro1CryptFormat，另外提供了Base64Format和HexFormat，对于有salt的密码请自定义实现ParsableHashFormat然后把salt格式化到散列值中；
	 * hashFormatFactory用于根据散列值得到散列的密码和salt；因为如果使用如SHA算法，那么会生成一个salt，此salt需要保存到散列后的值中以便之后与传入的密码比较时使用；默认使用DefaultHashFormatFactory；
	 * passwordMatcher使用PasswordMatcher，其是一个CredentialsMatcher实现；
	 * 将credentialsMatcher赋值给myRealm，myRealm间接继承了AuthenticatingRealm，其在调用getAuthenticationInfo方法获取到AuthenticationInfo信息后，
	 * 会使用credentialsMatcher来验证凭据是否匹配，如果不匹配将抛出IncorrectCredentialsException异常。
	 * 
	 * 如上方式的缺点是：salt保存在散列值中；没有实现如密码重试次数限制。
	 * @on
	 */
	@Test
	public void testDefaultPasswordService() {
		PasswordService passwordService = new DefaultPasswordService();
		HashService hashService = new DefaultHashService();
		ReflectionTestUtils.setField(passwordService, "hashService", hashService);

		HashFormat hashFormat = new Shiro1CryptFormat();
		ReflectionTestUtils.setField(passwordService, "hashFormat", hashFormat);

		HashFormatFactory hashFormatFactory = new DefaultHashFormatFactory();
		ReflectionTestUtils.setField(passwordService, "hashFormatFactory", hashFormatFactory);

		CredentialsMatcher passwordMatcher = new PasswordMatcher();
		ReflectionTestUtils.setField(passwordMatcher, "passwordService", passwordService);

		AuthorizingRealm realm = new MyRealm();
		ReflectionTestUtils.setField(realm, "passwordService", passwordService);
		ReflectionTestUtils.setField(realm, "credentialsMatcher", passwordMatcher);

		SecurityManager securityManager = new DefaultSecurityManager(realm);

		AuthenticationToken token = new UsernamePasswordToken("rico", "123");
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		subject.login(token);

	}
	
}
