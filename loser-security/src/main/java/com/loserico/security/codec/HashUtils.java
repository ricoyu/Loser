package com.loserico.security.codec;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.hash.Hashing;

/**
 * Utility for Secure hashes
 *
 *
 */
public final class HashUtils {

	public static String sha1(byte[] data) {
		return Hashing.sha1().hashBytes(data).toString();
	}

	public static String sha1(String data) {
		return Hashing.sha1().hashString(data, UTF_8).toString();
	}

	public static String sha256(byte[] data) {
		return Hashing.sha256().hashBytes(data).toString();
	}

	public static String sha256(String data) {
		return Hashing.sha256().hashString(data, UTF_8).toString();
	}

	/**
	 * Compute a MD5 hash using Java built-in MessageDigest, and format the result in
	 * hex. Beware that hash-computation is a CPU intensive operation
	 *
	 * @param data
	 * @return
	 */
	public static String md5(byte[] data) {
		return Hashing.md5().hashBytes(data).toString();
	}

	public static String md5(String data) {
		return Hashing.md5().hashString(data, UTF_8).toString();
	}
}