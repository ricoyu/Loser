package com.loserico.security;

/**
 * HashedCredentialsMatcher实现密码验证服务
 * 
 * Shiro提供了CredentialsMatcher的散列实现HashedCredentialsMatcher，和之前的PasswordMatcher不同的是，它只用于密码验证，
 * 且可以提供自己的盐，而不是随机生成盐，且生成密码散列值的算法需要自己写，因为能提供自己的盐。
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-02-19 20:57
 * @version 1.0
 *
 */
public class HashedCredentialsMatcherTest {
/*
	private static Connection connection;
	private static DataSource dataSource;
	
	@BeforeClass
	public static void setup() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(
				"jdbc:mysql://118.178.252.68:3306/test?rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8",
				"test", "test$123");
		dataSource = new SimpleDataSource(connection);
	}

	
	 * @of
	 * 生成密码散列值 
	 * 
	 * 此处我们使用MD5算法，“密码+盐（用户名+随机数）”的方式生成散列值：
	 * 如果要写用户模块，需要在新增用户/重置密码时使用如上算法保存密码，
	 * 将生成的密码及salt2存入数据库（因为我们的散列算法是：md5(md5(密码+username+salt2))）。
	 * 
	 * 生成的数据
	 * salt2: [a1de8a104c548df25afcc0d89627080d]
	 * encodedPassword: [c0a3956661cbe810ac3e7f3e2d709523]
	 * 
	 * @on
	 
	@Test
	public void testEncryptPassword() {
		String algorithmName = "md5";
		String username = "rico";
		String password = "123";
		String salt1 = username;
		String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
		int hashIterations = 2;

		Hash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);
		String encodedPassword = hash.toHex();
		System.out.println("salt2: [" + salt2 + "]");
		System.out.println("encodedPassword: [" + encodedPassword + "]");
	}

	
	 * @of
	 * 1、通过credentialsMatcher.hashAlgorithmName=md5指定散列算法为md5，需要和生成密码时的一样；
	 * 2、credentialsMatcher.hashIterations=2，散列迭代次数，需要和生成密码时的意义；
	 * 3、credentialsMatcher.storedCredentialsHexEncoded=true表示是否存储散列后的密码为16进制，需要和生成密码时的一样，默认是base64；
	 * 
	 * 此处最需要注意的就是HashedCredentialsMatcher的算法需要和生成密码时的算法一样。
	 * 另外HashedCredentialsMatcher会自动根据AuthenticationInfo的类型是否是SaltedAuthenticationInfo来获取credentialsSalt盐。
	 * @on
	 
	@Test
	public void testHashedCredentialsMatcher() {
		HashedCredentialsMatcher passwordMatcher = new HashedCredentialsMatcher();
		passwordMatcher.setHashAlgorithmName("MD5");
		passwordMatcher.setHashIterations(2);
		passwordMatcher.setStoredCredentialsHexEncoded(true);

		AuthorizingRealm realm = new MyRealm2();
		ReflectionTestUtils.setField(realm, "credentialsMatcher", passwordMatcher);

		SecurityManager securityManager = new DefaultSecurityManager(realm);

		AuthenticationToken token = new UsernamePasswordToken("admin", "123456");
//		AuthenticationToken token = new UsernamePasswordToken("rico", "123");
		SecurityUtils.setSecurityManager(securityManager);
		Subject subject = SecurityUtils.getSubject();
		subject.login(token);
	}*/
}
