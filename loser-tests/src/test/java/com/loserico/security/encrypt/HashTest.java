package com.loserico.security.encrypt;

import static org.junit.Assert.assertEquals;

import com.loserico.security.codec.HashUtils;
import com.loserico.security.service.PasswordHelper;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Assert;
import org.junit.Test;

public class HashTest {

	@Test
	public void testSHA512Hash() {
		/*
		 * String hashedPasswd = new Sha512Hash("ricoyu").toHex(); String base64Passwd
		 * = new Sha512Hash("ricoyu").toBase64(); System.out.println(hashedPasswd);
		 * System.out.println(base64Passwd); hashedPasswd = new Sha512Hash("ricoyu",
		 * "salt").toHex(); base64Passwd = new Sha512Hash("ricoyu",
		 * "salt").toBase64(); System.out.println(hashedPasswd);
		 * System.out.println(base64Passwd); hashedPasswd = new Sha512Hash("ricoyu",
		 * "salt", 6).toHex(); base64Passwd = new Sha512Hash("ricoyu", "salt",
		 * 6).toBase64(); System.out.println(hashedPasswd);
		 * System.out.println(base64Passwd);
		 */

		UsernamePasswordToken token = new UsernamePasswordToken("ricoyu", "123456");
		System.out.println(new Sha512Hash(token).toHex());
		System.out.println(new Sha512Hash(token).toHex());
		assertEquals(new Sha512Hash(token).toHex(), new Sha512Hash(token).toHex());
	}

	/*
	 * Shiro提供了base64和16进制字符串编码/解码的API支持，方便一些编码解码操作。Shiro内部的一些数据的存储/
	 * 表示都使用了base64和16进制字符串。
	 */
	@Test
	public void testBase64() {
		String str = "hello";
		String base64Encoded = Base64.encodeToString(str.getBytes());
		System.out.println(base64Encoded);
		String str2 = Base64.decodeToString(base64Encoded);
		System.out.println(str2);
		Assert.assertEquals(str, str2);
	}

	/*
	 * 还有一个可能经常用到的类CodecSupport，提供了toBytes(str, "utf-8") / toString(bytes,
	 * "utf-8")用于在byte数组/String之间转换。
	 */
	@Test
	public void testCodecSupport() {
		byte[] data = CodecSupport.toBytes("你好", "UTF-8");
		String s = CodecSupport.toString(data, "UTF-8");
		System.out.println(s);
	}

	/*
	 * 散列算法一般用于生成数据的摘要信息，是一种不可逆的算法，一般适合存储密码之类的数据，常见的散列算法如MD5、SHA等。一般进行散列时最好提供一个salt（盐）
	 * ，比如加密密码“admin”，产生的散列值是“21232f297a57a5a743894a0e4a801fc3”，
	 * 可以到一些md5解密网站很容易的通过散列值得到密码“admin”，即如果直接对密码进行散列相对来说破解更容易，此时我们可以加一些只有系统知道的干扰数据，
	 * 如用户名和ID（即盐）；这样散列的对象是“密码+用户名+ID”，这样生成的散列值相对来说更难破解。
	 */
	@Test
	public void testHashWithSalt() {
		String str = "hello";
		String salt = "123";
		String md5Str = new Md5Hash(str, salt).toString();
		//如上代码通过盐“123”MD5散列“hello”。另外散列时还可以指定散列次数
		String md5It2Str = new Md5Hash(str, salt, 2).toString();
		String md5Base64 = new Md5Hash(str, salt).toBase64();
		String md5Hex = new Md5Hash(str, salt).toHex();
		System.out.println("md5Str:   " + md5Str);
		System.out.println("md5Hex:   " + md5Hex);
		System.out.println("md5It2Str:   " + md5It2Str);
		System.out.println("md5Base64:   " + md5Base64);
	}

	/*
	 * 使用SHA256算法生成相应的散列数据，另外还有如SHA1、SHA512算法。
	 */
	@Test
	public void testSHA512Hash2() {
		String str = "hello";
		String salt = "123";
		String sha1 = new Sha256Hash(str, salt).toString();
		System.out.println(sha1);
	}

	/*
	 * Shiro还提供了通用的散列支持：通过调用SimpleHash时指定散列算法，其内部使用了Java的MessageDigest实现。
	 */
	@Test
	public void testSimpleHash() {
		String str = "hello";
		String salt = "123";
		//内部使用MessageDigest 
		String simpleHash = new SimpleHash("SHA-1", str, salt).toString();
		System.out.println(simpleHash);
		System.out.println(new SimpleHash("SHA-1", str, salt).toHex());
	}

	@Test
	public void testSha256() {
		String hashed = HashUtils.sha256("151501233801265");
		System.out.println(hashed);
	}
	
	@Test
	public void testCreateAdminPassword() {
		String raw = "123456";
		String hashed = HashUtils.sha256(raw);
		System.out.println(hashed);
		PasswordHelper passwordHelper = new PasswordHelper();
		String password = passwordHelper.encryptPassword(hashed, "#$deep%*Data_~123");
		System.out.println(password);
	}

	/*
	 * @of
	 * 1、首先创建一个DefaultHashService，默认使用SHA-512算法； 
	 * 2、可以通过hashAlgorithmName属性修改算法；
	 * 3、可以通过privateSalt设置一个私盐，其在散列时自动与用户传入的公盐混合产生一个新盐；
	 * 4、可以通过generatePublicSalt属性在用户没有传入公盐的情况下是否生成公盐；
	 * 5、可以设置randomNumberGenerator用于生成公盐； 6、可以设置hashIterations属性来修改默认加密迭代次数；
	 * 7、需要构建一个HashRequest，传入算法、数据、公盐、迭代次数。
	 * @on
	 */
	@Test
	public void testHashService() {
		DefaultHashService hashService = new DefaultHashService();//默认算法SHA-512 
		hashService.setHashAlgorithmName("SHA-512");
		hashService.setPrivateSalt(new SimpleByteSource("123"));//私盐，默认无
		hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false 
		hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个  
		hashService.setHashIterations(1);//生成Hash值的迭代次数

		HashRequest request = new HashRequest.Builder()
				.setAlgorithmName("MD5")
				.setSource(ByteSource.Util.bytes("hello"))
				.setSalt(ByteSource.Util.bytes("123"))
				.setIterations(2)
				.build();
		String hex = hashService.computeHash(request).toHex();
		System.out.println(hex);
	}

	/*
	 * SecureRandomNumberGenerator用于生成一个随机数
	 */
	@Test
	public void testRandomNumber() {
		SecureRandomNumberGenerator generator = new SecureRandomNumberGenerator();
		generator.setSeed("123".getBytes());
		String hex = generator.nextBytes().toHex();
		System.out.println(hex);
	}
}
