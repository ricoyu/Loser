package com.loserico.security.encrypt;

import org.junit.Test;

import com.loserico.security.codec.HashUtils;
import com.loserico.security.service.PasswordHelper;

public class PasswordHelperTest {

	@Test
	public void testEncryptPassword() {
		PasswordHelper passwordHelper = new PasswordHelper();
		String privateSalt = "5ab999941b5e322679ac3e85b96611de";
		String password = HashUtils.sha256("159357");
//		System.out.println("privateSalt: " + privateSalt);
		System.out.println(passwordHelper.encryptPassword(password, privateSalt));
	}
}
