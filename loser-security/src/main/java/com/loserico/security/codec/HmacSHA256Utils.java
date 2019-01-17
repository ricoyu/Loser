package com.loserico.security.codec;

import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-04-30 16:38
 * @version 1.0
 *
 */
public class HmacSHA256Utils {

	/**
	 * 使用指定的密码对内容生成消息摘要（散列值） 
	 * @param key
	 * @param content
	 * @return
	 */
	public static String digest(String key, String content) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte[] secretByte = key.getBytes("utf-8");
			byte[] dataBytes = content.getBytes("utf-8");

			SecretKey secret = new SecretKeySpec(secretByte, "HMACSHA256");
			mac.init(secret);

			byte[] doFinal = mac.doFinal(dataBytes);
			byte[] hexB = new Hex().encode(doFinal);
			return new String(hexB, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 使用指定的密码对整个Map的内容生成消息摘要（散列值）
	 * @param key
	 * @param map
	 * @return
	 */
	public static String digest(String key, Map<String, ?> map) {
		StringBuilder s = new StringBuilder();
		for (Object values : map.values()) {
			if (values instanceof String[]) {
				for (String value : (String[]) values) {
					s.append(value);
				}
			} else if (values instanceof List) {
				for (String value : (List<String>) values) {
					s.append(value);
				}
			} else {
				s.append(values);
			}
		}
		return digest(key, s.toString());
	}

}