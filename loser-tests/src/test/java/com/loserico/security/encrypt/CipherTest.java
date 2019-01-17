package com.loserico.security.encrypt;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;

public class CipherTest {

	@Test
	public void test256Cipher() throws IOException {
		AesCipherService aesCipherService = new AesCipherService();
		//使用256位AES加密
		aesCipherService.setKeySize(256);

		//创建一个测试密钥：
		byte[] testKey = aesCipherService.generateNewKey().getEncoded();
		//加密文件的字节：
		byte[] fileBytes = Files.readAllBytes(Paths.get("pom.xml"));
		ByteSource encrypted = aesCipherService.encrypt("123456".getBytes(), testKey);
		String content = new String(aesCipherService.decrypt(encrypted.getBytes(), testKey).getBytes());
		assertTrue(content.equals("123456"));
	}

	/*
	 * 加密/解密 Shiro还提供对称式加密/解密算法的支持，如AES、Blowfish等；当前还没有提供对非对称加密/解密算法支持
	 */
	@Test
	public void testAESCipher() {
		AesCipherService cipherService = new AesCipherService();
		cipherService.setKeySize(128);//设置key长度
		Key key = cipherService.generateNewKey();//生成key 
		String text = "hello";
		//加密
		String encryptedText = cipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
		System.out.println(encryptedText);
		//解密 
		String text2 = new String(cipherService.decrypt(Hex.decode(encryptedText), key.getEncoded()).getBytes());
		System.out.println(text2);
		assertEquals(text, text2);
	}
}
