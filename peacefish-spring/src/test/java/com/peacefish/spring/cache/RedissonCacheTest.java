package com.peacefish.spring.cache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:cache-redisson-config.xml")
public class RedissonCacheTest {

	@Resource
	private SimpleCacheService simpleCacheService;
	
	@Test
	public void testsimpleCacheRedisson() {
		String result1 = simpleCacheService.simpleCache("你好");
		System.out.println("======================================");
		System.out.println(result1);
	}
	
	@Test
	public void testCacheRemove() {
		String result1 = simpleCacheService.simpleCache("星巴克");
		simpleCacheService.removeSimpleCache("星巴克");
		String result2 = simpleCacheService.simpleCache("星巴克");
	}
	
	@Test
	public void testRedisCacheRedisson() {
		String result1 = simpleCacheService.redisCache("cache-key1");
		System.out.println("======================================");
		System.out.println(result1);
	}
	
	@Test
	public void testRedisCacheRemove() {
		String result1 = simpleCacheService.redisCache("星巴克");
		simpleCacheService.removeRedisCache("星巴克");
		String result2 = simpleCacheService.redisCache("星巴克");
	}
}
