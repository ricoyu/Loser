package com.loserico.concurrent.concurrenthashmap;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class ConcurrentHashMapTest {

	@Test
	public void testPutIfAbsent() {
		ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
		map.putIfAbsent("ricoyu", "handsome");
		map.putIfAbsent("ricoyu", "very handsome");
		assertEquals(map.get("ricoyu"), "handsome");
	}
}
