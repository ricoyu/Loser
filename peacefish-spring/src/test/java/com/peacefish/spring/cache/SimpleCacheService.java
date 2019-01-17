package com.peacefish.spring.cache;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;

import org.springframework.stereotype.Service;

@Service
public class SimpleCacheService {

	@CacheResult(cacheName="simpleCaches")
	public String simpleCache(String msg) {
		String result = "Hello simple cache: " + msg;
		System.out.println(result);
		return result;
	}
	
	@CacheRemove(cacheName="simpleCaches")
	public void removeSimpleCache(String msg) {
		System.out.println("删除缓存的: " + msg);
	}
	
	@CacheResult(cacheName="redisCache")
	public String redisCache(String msg) {
		String result = "Hello redis cache: " + msg;
		System.out.println(result);
		return result;
	}
	
	@CacheRemove(cacheName="redisCache")
	public void removeRedisCache(String msg) {
		System.out.println("删除缓存的: " + msg);
	}
	
	@CacheResult(cacheName="spring:cache:user")
	public String registerUser(String name) {
		System.out.println("注册: " + name);
		return name;
	}
}
