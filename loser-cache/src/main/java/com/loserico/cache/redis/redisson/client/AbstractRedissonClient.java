package com.loserico.cache.redis.redisson.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.redisson.api.RedissonClient;

import com.loserico.cache.exception.RedissonClientCreationException;
import com.loserico.cache.redis.redisson.config.RedissonFactory;
import com.loserico.cache.redis.redisson.config.RedissonProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * RedissonClient 代理类
 * <p>
 * Copyright: Copyright (c) 2019-06-03 15:28
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public abstract class AbstractRedissonClient {

	private boolean lenientFallback = true;

	private Map<String, RedissonClient> targetClients = new HashMap<>();

	private Map<String, RedissonProperties> redissonPropertiesMap = new HashMap<>();

	private RedissonClient defaultRedissonClient;

	private String defaultRedissonClientName;

	protected AbstractRedissonClient() {
		log.info("Do nothing in AbstractRedissonClient(Config)");
	}

	/**
	 * Specify whether to apply a lenient fallback to the default Pool if no specific Pool could be
	 * found for the current lookup key.
	 * <p>
	 * Default is "true", accepting lookup keys without a corresponding entry in the target Pool map
	 * - simply falling back to the default Pool in that case.
	 * <p>
	 * Switch this flag to "false" if you would prefer the fallback to only apply if the lookup key
	 * was {@code null}. Lookup keys without a Pool entry will then lead to an
	 * IllegalStateException.
	 * 
	 * @see #setTargetPools
	 * @see #setDefaultTargetPool
	 * @see #determineCurrentLookupKey()
	 */
	public void setLenientFallback(boolean lenientFallback) {
		this.lenientFallback = lenientFallback;
	}

	/**
	 * Determine the current lookup key. This will typically be implemented to check a thread-bound
	 * transaction context.
	 * <p>
	 * Allows for arbitrary keys. The returned key needs to match the stored lookup key type, as
	 * resolved by the method.
	 */
	protected abstract Object determineCurrentLookupKey();

	/**
	 * Retrieve the current target client. Determines the
	 * {@link #determineCurrentLookupKey() current lookup key}, performs
	 * a lookup in the {@link #setTargetClients targetClients} map,
	 * falls back to the specified
	 * {@link #setDefaultRedissonClient default redisson client} if necessary.
	 * @see #determineCurrentLookupKey()
	 * @on
	 */
	public RedissonClient determineTargetClient() {
		Object lookupKey = determineCurrentLookupKey();
		log.info("Current redisson client {}", lookupKey);
		RedissonClient redissonClient = this.targetClients.get(lookupKey);
		if (redissonClient == null && (this.lenientFallback || lookupKey == null)) {
			String defaultClientName = defaultRedissonClientName.toLowerCase();
			log.info("Switch to default redisson client {}", defaultClientName);
			redissonClient = this.targetClients.get(defaultClientName);
		}
		if (redissonClient == null) {
			throw new IllegalStateException(
					"Cannot determine target redisson client for lookup key [" + lookupKey + "]");
		}
		return redissonClient;
	}

	public Map<String, RedissonClient> getTargetClients() {
		return targetClients;
	}

	public void setTargetClients(Map<String, RedissonClient> targetClients) {
		this.targetClients = targetClients;
	}

	public Map<String, RedissonProperties> getRedissonPropertiesMap() {
		return redissonPropertiesMap;
	}

	public void setRedissonPropertiesMap(Map<String, RedissonProperties> redissonPropertiesMap) {
		this.redissonPropertiesMap = redissonPropertiesMap;
	}

	public RedissonClient getDefaultRedissonClient() {
		return defaultRedissonClient;
	}

	public void setDefaultRedissonClient(RedissonClient defaultRedissonClient) {
		this.defaultRedissonClient = defaultRedissonClient;
	}

	public String getDefaultRedissonClientName() {
		return defaultRedissonClientName;
	}

	public void setDefaultRedissonClientName(String defaultRedissonClientName) {
		this.defaultRedissonClientName = defaultRedissonClientName;
	}

	public boolean isLenientFallback() {
		return lenientFallback;
	}

	@PostConstruct
	public void init() {
		if (redissonPropertiesMap != null && !redissonPropertiesMap.isEmpty()) {
			Set<Entry<String, RedissonProperties>> entrySet = redissonPropertiesMap.entrySet();
			int totalClients = entrySet.size();
			log.info("There are {} RedissonClient to be created", totalClients);
			CountDownLatch countDownLatch = new CountDownLatch(totalClients);
			ExecutorService executorService = Executors.newFixedThreadPool(totalClients);

			Map<String, Future<RedissonClient>> futureMap = new HashMap<>();
			for (Entry<String, RedissonProperties> entry : entrySet) {
				Future<RedissonClient> redissonClientFuture = (Future<RedissonClient>) executorService.submit(() -> {
					try {
						RedissonClient redissonClient = RedissonFactory.createRedissonClient(entry.getValue());
						log.info("Created RedissonClient[{}]", entry.getKey());
					} finally {
						countDownLatch.countDown();
					}
				});
				futureMap.put(entry.getKey(), redissonClientFuture);
			}

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				log.error("", e);
				throw new RedissonClientCreationException(e);
			}

			try {
				for (Entry<String, Future<RedissonClient>> entry : futureMap.entrySet()) {
					targetClients.put(entry.getKey(), entry.getValue().get());
				}
			} catch (InterruptedException | ExecutionException e) {
				log.error("", e);
				throw new RedissonClientCreationException(e);
			}
		}
	}
}
