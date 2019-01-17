package org.loser.cache.concurrent;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.collection.ConcurrentMap;

public class MapTest2 {

	public static void main(String[] args) {
		ConcurrentMap<Long, String> map = RedissonUtils.concurrentMap("students");
		map.put(101L, "Justin Oh Yuzhen2");
		System.out.println(map.get(100L));
		System.out.println(map.get(101L));
		System.out.println(map.get(102L));
		map.put(102L, "Justin Oh Yuzhen2");
		System.out.println(map.get(101L));
	}
}
