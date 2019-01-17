package com.loserico.commons.codec;

public class HexUtils {
	
	private static final char[] hexCode = "0123456789abcdef".toCharArray();

	/**
	 * byte[]转16进制字符串
	 * @param data
	 * @return String
	 */
	public static String bytesToHex(byte[] data) {
		StringBuilder r = new StringBuilder(data.length * 2);
		for (byte b : data) {
			r.append(hexCode[(b >> 4) & 0xF]);
			r.append(hexCode[(b & 0xF)]);
		}
		return r.toString();
	}
}
