package com.loserico.utils;

import org.junit.Test;

import com.loserico.commons.utils.MathUtils;

public class MathUtilsTest {

	@Test
	public void testRound() {
		String val = MathUtils.format(1305.4d, 2);
		System.out.println(val);
		
	}
}
