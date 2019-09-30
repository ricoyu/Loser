package com.loserico.commons.codec;

import java.math.BigInteger;

import org.junit.Test;

public class HexUtilsTest {

	@Test
	public void testHexToDecimal() {
		System.out.println(RedixUtils.hexToDecimal("0xffffcd7d"));
		System.out.println(RedixUtils.hexToDecimal("0xf2"));
		System.out.println(RedixUtils.hexToDecimal("0xf21"));
		System.out.println(RedixUtils.hexToDecimal("0xf"));
	}
	
	@Test
	public void testHexToBin() {
		// System.out.println(RedixUtils.hexToBinary("ffffcd7d"));
		// System.out.println(BigInteger.valueOf(0xffffcd7d).toString(2));
	}
	
	@Test
	public void testIntToBinary() {
		int i = 4 << 15;
		System.out.println(RedixUtils.intToBinary(4));
		System.out.println(RedixUtils.intToBinary(i));
	}
	
	@Test
	public void testName() {
		String s = HexUtils.hexToString("282956");
		System.out.println(s);
	}
}
