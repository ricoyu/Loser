package com.loserico.security.encrypt;

import static org.junit.Assert.*;

import java.security.Key;
import java.security.MessageDigest;
import java.text.MessageFormat;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Test;

public class EncryptTest {
	
	/**
	 * base64编码/解码操作
	 */
	@Test
	public void testEncryptBase64() {
		String s = "yuxuehua";
		String base64Encoded = Base64.encodeToString(s.getBytes());
		String decodedStr = Base64.decodeToString(base64Encoded);
		assertTrue(s.equals(decodedStr));
		System.out.println("base64Encoded:[" + base64Encoded + "]");
		System.out.println("decodedStr:[" + decodedStr + "]");
		
		String sql = "select a, b, c, d from ajjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjaa where a=1 and b=2 order by a desc";
		System.out.println(Base64.encodeToString(sql.getBytes()));
		System.out.println(Base64.encodeToString(sql.getBytes()));
		System.out.println(Base64.encodeToString(sql.getBytes()));
	}

	/**
	 * 16进制字符串编码/解码
	 */
	@Test
	public void testEncryptHex() {
		String s = "yuxuehua";
		String hexEncoded = Hex.encodeToString(s.getBytes());
		String hexDecoded = new String(Hex.decode(hexEncoded));
		assertTrue(s.equals(hexDecoded));
		System.out.println(hexEncoded);
		System.out.println(MessageFormat.format("hexEncoded:[{0}], hexDecoded:[{1}]", hexEncoded, hexDecoded));
	}

	@Test
	public void testCodecSupport() {
		String s = "yuxuehua";
		String hexEncoded = Hex.encodeToString(CodecSupport.toBytes(s));
		String decodedStr = CodecSupport.toString(Hex.decode(hexEncoded));
		System.out.println(decodedStr);
	}

	@Test
	public void testMd5Hash() {
		String s = "yuxuehua";
		String salt = "123";

		String md5 = new Md5Hash(s, salt).toString();
		String md52 = new Md5Hash(s, salt, 2).toString();//设置散列次数
		String md522 = new Md5Hash(new Md5Hash(s, salt), salt).toString();
		assertTrue(md52.equals(md522));
		System.out.println(MessageFormat.format("md5:[{0}]", md5));
		System.out.println(MessageFormat.format("md52:[{0}]", md52));
	}

	@Test
	public void testSha256() {
		String s = "yuxuehua";
		String salt = "123";
		String sha1 = new Sha256Hash(s, salt).toString();
		System.out.println(MessageFormat.format("Sha256[{0}]", sha1));

	}

	@Test
	public void testSimpleHash() {
		String str = "hello";
		String salt = "123";
		//内部使用MessageDigest  
		String simpleHash = new SimpleHash("SHA-1", str, salt).toString();
		System.out.println(MessageFormat.format("simpleHash[{0}]", simpleHash));
	}

	@Test
	public void testDefaultHashService() {
		DefaultHashService hashService = new DefaultHashService();
		hashService.setHashAlgorithmName("SHA-512");//默认算法SHA-512
		hashService.setPrivateSalt(new SimpleByteSource("123"));//私盐，默认无 
		hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false 
		hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个  
		hashService.setHashIterations(1);//生成Hash值的迭代次数

		HashRequest request = new HashRequest.Builder().setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
				.setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();
		String hex = hashService.computeHash(request).toHex();
		System.out.println(MessageFormat.format("hex[{0}]", hex));

	}

	@Test
	public void testAesCipher() {
		AesCipherService aesCipherService = new AesCipherService();
		aesCipherService.setKeySize(128);//设置key长度
		Key key = aesCipherService.generateNewKey();
		String text = "hello";
		//加密
		String encryptedText = aesCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
		//解密
		String deCryptedText = CodecSupport
				.toString(aesCipherService.decrypt(Hex.decode(encryptedText), key.getEncoded()).getBytes());
		System.out.println(MessageFormat.format("deCryptedText[{0}]", deCryptedText));
		assertTrue(text.equals(deCryptedText));
	}
}
