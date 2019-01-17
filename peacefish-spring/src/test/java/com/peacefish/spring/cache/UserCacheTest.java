package com.peacefish.spring.cache;

import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserCacheTest {
	
	private static final Logger logger = LoggerFactory.getLogger(UserCacheTest.class);

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("cache-ehcache-config.xml");
//		ApplicationContext context = new ClassPathXmlApplicationContext("cache-redisson-config.xml");
		SimpleCacheService cacheService = context.getBean(SimpleCacheService.class);
		for (int i = 0; i < 6; i++) {
			cacheService.registerUser("俞雪华");
			System.out.println("===========================");
//			try {
//				System.out.println("等待2秒再重新注册");
//				SECONDS.sleep(21);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
}
