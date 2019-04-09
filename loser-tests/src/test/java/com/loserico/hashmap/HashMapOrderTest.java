package com.loserico.hashmap;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;

/**
 * 
 * <p>
 * Copyright: Copyright (c) 2019-01-22 13:39
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class HashMapOrderTest {

	/*
	 * Several applications rely in the fact that hash map entries are retrieved in
	 * the same order that they were inserted in the map. This was never assured by
	 * the java.util.HashMap but some programmers ignored it and built their
	 * programs assuming that the iteration order will be historical. Using java 7
	 * entries will be retrieved in the same way that they were inserted (more or
	 * less). The following program shows the differences when using linked hash
	 * maps and normal hash maps in the iteration order
	 * 
	 * We can appreciate that the order in the hash map implementation is not
	 * predictable. In case the order of iteration is dependent on the historical
	 * insertion order of the hash map, the class java.util.LinkedHashMap should be
	 * used, since this class guarantees the iteration order.
	 */
	@Test
	public void testHashValueOrders() {
		// Using HashMap
		System.out.println("Using plain hash map with balanced trees:");

		HashMap<String, String> stringMap = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			stringMap.put("index+" + i, String.valueOf(i));
		}

		stringMap.values().forEach(System.out::println); // 无序

		// Using LinkedHashMap
		System.out.println("Using LinkedHashMap:");
		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
		for (int i = 0; i < 100; ++i) {
			linkedHashMap.put("index_" + i, String.valueOf(i));
		}

		linkedHashMap.values().forEach(System.out::println); // 有序
	}

}
