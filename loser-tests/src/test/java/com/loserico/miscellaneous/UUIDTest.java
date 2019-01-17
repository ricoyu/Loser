package com.loserico.miscellaneous;

import java.util.UUID;

import org.junit.Test;

public class UUIDTest {

	@Test
	public void testGenUUID() {
		System.out.println(UUID.randomUUID().toString());
	}
}
