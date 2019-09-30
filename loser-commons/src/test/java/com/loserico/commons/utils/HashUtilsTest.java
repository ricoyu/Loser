package com.loserico.commons.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright: (C), 2019 2019-09-25 9:54
 * <p>
 * Company: Sexy Uncle Inc.
 *
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 */
public class HashUtilsTest {

	@Test
	public void testHash7() {
		List<String> userIds = new ArrayList<>();
		userIds.add("999111001");
		userIds.add("999169010");
		userIds.add("230010001");
		userIds.add("410010001");

		for(String userId : userIds) {
			System.out.println(HashUtils.hash7(userId));
			System.out.println(HashUtils.hash7(userId));
			System.out.println("-----------------------");
		}

		System.out.println("\n===============================\n");
		for(String userId : userIds) {
			int hash1 = HashUtils.hash8(userId);
			System.out.println(hash1);
			System.out.println(HashUtils.hash8(userId));
			System.out.println(hash1 % 4);
			System.out.println("-----------------------");
		}

		System.out.println("\n===============================\n");
		for(String userId : userIds) {
			int hash1 = HashUtils.fnvHash(userId);
			System.out.println(hash1);
			System.out.println(HashUtils.fnvHash(userId));
			System.out.println(hash1 % 4);
			System.out.println("-----------------------");
		}
	}
}
