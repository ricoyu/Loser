package com.loserico.security;

import org.junit.Test;

import com.loserico.security.codec.HashUtils;
import com.loserico.security.service.PasswordHelper;

public class PasswordHelperTest {

	@Test
	public void testGenPassword() {
		String password = HashUtils.sha256("yxhhsy666");
		PasswordHelper passwordHelper = new PasswordHelper();
		String privateSalt = passwordHelper.privateSalt();
		System.out.println("Salt: " + privateSalt);
		password = passwordHelper.encryptPassword(password, privateSalt);
		System.out.println("密码: "+password);

	}
	
	@Test
	public void testGenPwd() {
		String password = HashUtils.sha256("139621336227896");
		String privateSalt = "d28e12acdb4a1c2d00f126ab9b1c2c06";
		PasswordHelper passwordHelper = new PasswordHelper();
		password = passwordHelper.encryptPassword(password, privateSalt);
		System.out.println(password);
	}
	
	@Test
	public void testName() {
		System.out.println(HashUtils.sha256("654321"));
	}

}
