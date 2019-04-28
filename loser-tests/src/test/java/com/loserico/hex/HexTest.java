package com.loserico.hex;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.loserico.commons.codec.MD5Utils;

public class HexTest {

	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
	
	@Test
	public void testInt2Hex() {
		StringBuilder sb = new StringBuilder();
		byte[] bs = DigestUtils.md5("你好");
		for (int i = 0; i < bs.length; i++) {
			int c = bs[i] & 0xff;
			if (c < 16) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(c));
		}
		System.out.println(sb.toString());
	}
	
	@Test
	public void test2() {
		byte[] a = new byte[10];
		a[0] = -127;
		System.out.println(a[0]);
		System.out.println(Integer.toBinaryString(a[0]));
		int i = a[0] & 0xff;
		System.out.println(i);
	}

	@Test
	public void testIntegerMax() {
		int i = Integer.MAX_VALUE;
		System.out.println(i + "的二进制形式" +Integer.toBinaryString(i));
		int min = Integer.MIN_VALUE;
		System.out.println(min + "的二进制形式" +Integer.toBinaryString(min));
	}
	@Test
	public void testHex2Int() {
		int i = 0xff; // 15 * 16 + 15 == 255
		System.out.println(i);
		System.out.println(Integer.toBinaryString(i)); // 1111 1111
	}
	
	@Test
	public void testByte2Int() {
		byte b = -0b1;
		int i = b;
		System.out.println("i="+i+", 二进制形式="+Integer.toBinaryString(i));
		System.out.println("i="+i+", 二进制形式="+Integer.toBinaryString(i & 0xff));
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length << 2); // 左移2位就是 data.length * 4
		for (byte x : bytes) {
			// byte只有8位, int是32位, x& oxff之后会转换为int类型
			int high = (x & 0xf0) >> 4; // 处理高四位
			int low = x & 0x0f; // 处理低四位

			sb.append(HEX_CHARS[high]);
			sb.append(HEX_CHARS[low]);
		}
		return sb.toString().trim();
	}

	public static float toFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		System.out.println(accum);
		return Float.intBitsToFloat(accum);
	}
}
