package com.loserico.security;
import org.junit.Test;

import com.loserico.security.codec.Rsa;

public class RsaTest {

	@Test
	public void testName() {
		
	}
	
	@Test
	public void testPublicEncryptPrivateDecrypt() {
		Rsa rsa = Rsa.instance();
		String data = "你好，这是RSA非对称加密测试";
		//		String data = "hello world";

		String encrypted = rsa.publicEncrypt(data);
		System.out.println(encrypted);
		
		//System.out.println(rsa.publicKey());
		String encrypted2 = rsa.publicEncrypt(data, rsa.publicKey());
		System.out.println(encrypted2);
		
		String decrypted = rsa.privateDecrypt(encrypted);
		System.out.println(decrypted);
		String decrypted2 = rsa.privateDecrypt(encrypted2);
		System.out.println(decrypted);
		
		//String dataFromJs = "C266RxLcM5xGeZbn25AeZL7N1v8y8WCUZLDd22DYvPO9Taqf0Q/1sW3uvzINCDMB5PoUR/lu03BdsrSa+ETIX4g0NRkRrh66M3V2anRTh51ZitDgtMhgw3yf9h4atMyNojLbXVoZG7Kk2jS34LIr4kAhz1yMhHOnZr16SlNdj0g=";
		String dataFromJs = "pqWbiDAcchKhmCnxQWJ9Z4Y86dfp0NjTFl162BD3W+CwGshEJSWiYawe8xTY9eqbqCSBTWojD71y0HE2jkyMDp9+TTMywHVBqB/jRVxPh3Pr2jdPUk9+SmovdMOOF8uresoZn3FuIxo+pe04nvigoCBSPKe51cGMeF94FxiMFZCfrmrmTER68v7hMrOt7mk5tCGMzgJCNVQ51CpGEn1cMD5/kmA3gn9DNOfmDMKESRbIWDbmTlGRmrsgFXL3IysuiRTdylTktJNb1LEnRrxaFFdBsKldd4kyZvSOXeS7iWYgL+yyBqo0DLc8ZtO07FWTSvHbib4OSNmA0V4R/Q75+w==";
		System.out.println(rsa.privateDecrypt(dataFromJs));
	}
	
	/**
	 * 测试解密token出错问题
	 * 参数：
	 * timestamp: 1534323558718
	 * 原始token: 8B9OkolEYIFR807wLTLModwJJSypMMVVW2i3haoiWWBpSOLEoGqSZygtn4WYLyCQz8
	 * uri: api/v1/resources
	 * 前端log中记录的加密后的token： 94GGjaCSiLL3LU/nlHsH1aVfMpFIsOcBTz/EkRGCA1bqH3pxOrPnB4ck4Mfpv4bzaqH+zO9Q/CiB4+I+WRbWmeBZBblZ9pNWb3vCs/YADVTSpShdMFSvTRFqT+hdS4TdOd8chQrugN9f5xPKzrH6wH6qXHhZxij2FL3X9DsgeQ1hsLBvU2xhtGQ90aKCuneWTqUlH/TDr7dMtYfYj2phcluo6kQ9cr85Sh6rbruuJEgQjP07v37ykkPH1iC17S+q3VdzzCND4Y2v7tCpVj5rITUezdQZjxFSEnJZHn9Uj76DiPtQErr5/Vyz3JJTNUWFGtXOsYGv6Ukg86iY7M8l
	 */
	@Test
	public void testTokenInvalid() {
		//String expectedToken = "94GGjaCSiLL3LU/nlHsH1aVfMpFIsOcBTz/EkRGCA1bqH3pxOrPnB4ck4Mfpv4bzaqH+zO9Q/CiB4+I+WRbWmeBZBblZ9pNWb3vCs/YADVTSpShdMFSvTRFqT+hdS4TdOd8chQrugN9f5xPKzrH6wH6qXHhZxij2FL3X9DsgeQ1hsLBvU2xhtGQ90aKCuneWTqUlH/TDr7dMtYfYj2phcluo6kQ9cr85Sh6rbruuJEgQjP07v37ykkPH1iC17S+q3VdzzCND4Y2v7tCpVj5rITUezdQZjxFSEnJZHn9Uj76DiPtQErr5/Vyz3JJTNUWFGtXOsYGv6Ukg86iY7M8l";
		String expectedToken = "GHg72+GERMDbpu0+4hQVfkW20CkuZaF3PePHlsSbuuQt9hTOSydeng5BlFIlxM4x/LPlpg8x8CY2g0AerhDpAfh86Vx2Olhy6evy9Ybr74QIISF1PNnFDJ8TNweE0Be//seJAeUKOn7HXHE0mb4iKoKqPOwIdMGRufXA4oiy+BuLjf6N0VD3KE3ZOyCTMS0y56ca5b5/1Rt+HE5GODsASiTcVpvjRpQxDfG+oQVv0wRPqI9kydo27Go4LlSdltCGkinBys68760WThd+FhbYSxY8NOv02fBUMaIHWl1PXig2jhrr0+ycX3+gtQQBnqf8kn9GDQJULCot/Bt+Z67h3Q==";
		
		long timestamp = 1534323558718L;
		String originalToken = "8B9OkolEYIFR807wLTLModwJJSypMMVVW2i3haoiWWBpSOLEoGqSZygtn4WYLyCQz8";
		String uri = "api/v1/resources";
		String toEncrypt = "uri="+uri+"&access_token="+originalToken+"&timestamp="+timestamp;
		
		Rsa rsa = Rsa.instance();
		
		String encryptedToken = rsa.publicEncrypt(toEncrypt);
		System.out.println("expectedToken: " + expectedToken);
		System.out.println("encryptedToken: " + encryptedToken);
		System.out.println(expectedToken.equals(encryptedToken));
		
		System.out.println("\n=====================================");
		String decrypted = rsa.privateDecrypt(encryptedToken);
		System.out.println("decrypted: " + decrypted);
		String decryptedExpected = rsa.privateDecrypt(expectedToken);
		System.out.println("decryptedExpected: " + decryptedExpected);
		
	}
	
	@Test
	public void testRsaJs() {
		String encryptedByJs = "0f447ff6da53ea514b991fd3d640292436ee8b8538b0168f12bf31272b4994c7f8acfd13a89b51644790775cf7253834918a89661159b4016ea25077d862470fc72a7780c592f4d70a3cd9c25bf89b42b808f041e31a70e1cff63ee01b04c26b2851dac8318d9177e708";
		Rsa rsa = Rsa.instance();
		
		String plainText = rsa.privateDecrypt(encryptedByJs);
		System.out.println(plainText);
	}
}