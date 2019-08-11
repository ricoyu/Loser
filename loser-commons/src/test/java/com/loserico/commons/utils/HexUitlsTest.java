package com.loserico.commons.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.loserico.commons.codec.HexUtils;

public class HexUitlsTest {

	@Test
	public void testHexToString() {
		String hex = "636f6d2f6c6f73657269636f2f6a766d2f54657374436c617373";
		String str = HexUtils.hexToString(hex);
		System.out.println(str);
		assertEquals("com/loserico/jvm/TestClass", str);
	}
	
	@Test
	public void testHexToString2() {
		String hex = "6a61 7661 2f6c 616e 672f 4f62 6a65 6374";
		String str = HexUtils.hexToString(hex);
		System.out.println(str);
		assertEquals("java/lang/Object", str);
	}
}
