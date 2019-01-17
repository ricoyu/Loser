package org.loser.cache.atomic;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.concurrent.atomic.AtomicLong;

public class AtomicLongTest2 {

	public static void main(String[] args) {
		AtomicLong atomicLong = RedissonUtils.atomicLong("atomicLongTest");
		long initialValue = atomicLong.get();
		System.out.println("initialValue: " + initialValue);
		long incrByOne = atomicLong.incrementAndGet();
		System.out.println("incrByOne: " + incrByOne);
	}
}
