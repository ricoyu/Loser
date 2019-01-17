package com.loserico;

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
		String dataFromJs = "JRpjKJwpbfvMegulmmFcD8C3t061j1HKse7tKGNbZIh7e4Aj4jm5hNCSYuFULPiuPk%2BYRtOisltOFRyJh4EqFgfkClIVSdVrImTvFTLXG9xEBtj2f3G1P7l9hnMZKaiXq3TnZff%2FvheI3W0O8jWYX9xLnxZE%2F8tXjZtvfA81XPcRduB4hI2sCKJNq%2FYeG9gu5NOkoAM8xfbt6phqRemMJ9w41gnbYI%2BG5jpa7Z9pH6HFpplMZTkW1zJHdeDtEYbFcVubx%2Bp%2FAS%2F5lFCKawDT%2F5MLy2qFwXKb1iN5Oum4DfvOkhJJWIVdMQSpN2TQ5bLqwKQjPSWXjACDku3ScWCLeQ%3D%3D";
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
		String expectedToken = "94GGjaCSiLL3LU/nlHsH1aVfMpFIsOcBTz/EkRGCA1bqH3pxOrPnB4ck4Mfpv4bzaqH+zO9Q/CiB4+I+WRbWmeBZBblZ9pNWb3vCs/YADVTSpShdMFSvTRFqT+hdS4TdOd8chQrugN9f5xPKzrH6wH6qXHhZxij2FL3X9DsgeQ1hsLBvU2xhtGQ90aKCuneWTqUlH/TDr7dMtYfYj2phcluo6kQ9cr85Sh6rbruuJEgQjP07v37ykkPH1iC17S+q3VdzzCND4Y2v7tCpVj5rITUezdQZjxFSEnJZHn9Uj76DiPtQErr5/Vyz3JJTNUWFGtXOsYGv6Ukg86iY7M8l";
		
		long timestamp = 1534323558718L;
		String originalToken = "8B9OkolEYIFR807wLTLModwJJSypMMVVW2i3haoiWWBpSOLEoGqSZygtn4WYLyCQz8";
		String uri = "api/v1/resources";
		String toEncrypt = "uri="+uri+"&access_token="+originalToken+"&timestamp="+timestamp;
		
		Rsa rsa = Rsa.instance();
		
		String encryptedToken = rsa.publicEncrypt(toEncrypt);
		System.out.println(expectedToken.equals(encryptedToken));
	}
}