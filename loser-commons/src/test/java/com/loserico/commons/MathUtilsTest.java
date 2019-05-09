package com.loserico.commons;

import org.junit.Test;

import com.loserico.commons.utils.MathUtils;

public class MathUtilsTest {

	
	@Test
	public void testFormatDouble() {
		double s = 123.99123123d;
		Double d2 = MathUtils.formatDouble(s, 0);
		System.out.println(d2);
	}
}
