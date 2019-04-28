package com.loserico.hex;

import org.junit.Test;

/**
 * byte 和  hex 互转, 以及为什么hex不满2位的前面要补0
 * 
 * https://www.baeldung.com/java-byte-arrays-hex-strings
 * <p>
 * Copyright: Copyright (c) 2019-04-23 14:22
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ByteHexTest {

	public static void main(String[] args) {
		byte b = -45;
		System.out.println(byteToHex(b));
	}

	public static String byteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}

	public byte hexToByte(String hexString) {
		int firstDigit = toDigit(hexString.charAt(0));
		int secondDigit = toDigit(hexString.charAt(1));
		return (byte) ((firstDigit << 4) + secondDigit);
	}

	private int toDigit(char hexChar) {
		int digit = Character.digit(hexChar, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
		}
		return digit;
	}

	@Test
	public void testByteMaxMin() {
		byte min = 45;
		String complement = Integer.toBinaryString((int) min);
		System.out.println(complement);
	}

	@Test
	public void testHex2Decimal() {
		int i = 0xad;
		System.out.println(i);
	}
}
