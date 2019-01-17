package com.peacefish.spring.cache;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:cache-config.xml")
public class SimpleCacheTest {
	
	@Resource
	private SimpleCacheService simpleCacheService;

	@Test
	public void testSimpleCache() {
		String result1 = simpleCacheService.simpleCache("星巴克");
		String result2 = simpleCacheService.simpleCache("星巴克");
		String result3 = simpleCacheService.simpleCache("自家咖啡");
		System.out.println("======================================");
		System.out.println(result1);
		System.out.println(result2);
		System.out.println(result3);
	}
}
