package com.loserico.miscellaneous;

import org.junit.Test;

public class EnvTest {

	@Test
	public void testGetEnv() {
		String redisHost = System.getenv("Path");
		System.out.println(redisHost);
	}
	
	@Test
	public void testUserDir() {
		System.out.println(System.getProperty("user.dir"));
	}
}
