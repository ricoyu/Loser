package com.loserico.cache.redis;

import static com.loserico.cache.redis.status.hash.HSet.INSERTED;
import static com.loserico.cache.redis.status.hash.HSet.UPDATED;
import static com.loserico.cache.utils.KeyUtils.joinKey;
import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.loserico.cache.auth.LoginResult;
import com.loserico.cache.exception.JedisValueOperationException;
import com.loserico.cache.exception.NoJedisPoolException;
import com.loserico.cache.redis.collection.QueueListener;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.factory.JedisPoolFactories;
import com.loserico.cache.redis.listeners.MessageListener;
import com.loserico.cache.redis.listeners.UnSubsccribeListener;
import com.loserico.cache.redis.pool.RoutingRedisPool;
import com.loserico.cache.redis.pure.concurrent.NonBlockingLock;
import com.loserico.cache.redis.status.hash.HSet;
import com.loserico.cache.redis.status.hash.TTL;
import com.loserico.cache.resource.PropertyReader;
import com.loserico.cache.spring.ApplicationContextHolder;
import com.loserico.cache.utils.IOUtils;
import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.jsonpath.JsonPathUtils;
import com.loserico.commons.utils.PrimitiveUtils;
import com.peacefish.spring.concurrent.ConcurrentTemplate;
import com.peacefish.spring.transaction.TransactionEvents;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.Pool;

/**
 * Jedis 的工具类, key/value 支持任意类型
 * 
 * 通过lua脚本实现了一些原生Redis不具备的接口, 如带过期时间的setnx(key, value, expires, timeUnit)
 * 
 * <p>
 * Copyright: Copyright (c) 2018-05-12 18:16
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 *
 */
@SuppressWarnings("unchecked")
public final class JedisUtils {

	private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);

	public static final String STATUS_SUCCESS = "OK";

	private static final String NON_BLOCKING_LOCK_PREDIX = "$lock:";
	private static final String NON_BLOCKING_LOCK_SUFFIX = ":nblk$";

	/**
	 * 直接在redis.properties中配置好redis.host, redis.port等参数会创建的Pool 
	 * <p/>这种情况JedisUtils连接的只有这一个数据源.
	 * 
	 * 可以通过 redis.default.enabled=false 显式禁用defaultPool.
	 * 如果为routingRedisPool做了配置, 那么defaultPool也会禁用, 二选一
	 * @on
	 */
	private static volatile Pool<Jedis> defaultPool = null;

	/**
	 * 如果应用需要连接多个Redis Instance或者同一个Instance的多个database时, 可以选择配置RoutingRedisPool
	 * 跟上面的defaultPool是二选一的关系. 
	 * 
	 * <pre>{@code
	 * cxkg.redis.host=192.168.102.103
	 * cxkg.redis.port=6379
	 * cxkg.redis.password=123456
	 * cxkg.redis.database=2
	 * 
	 * kangan.redis.host=192.168.102.103
	 * kangan.redis.port=6379
	 * kangan.redis.password=123456
	 * kangan.redis.database=3
	 * 
	 * 然后配置RedisProperties
	 * @Configuration
	 * @PropertySource("redis.properties")
	 * public class CacheConfig {
	 * 
	 * @Bean
	 * @ConfigurationProperties(prefix = "cxkg.redis")
	 * public RedisProperties cxkgProperties() {
	 * 	return new RedisProperties();
	 * }
	 * 
	 * @Bean
	 * @ConfigurationProperties(prefix = "kangan.redis")
	 * public RedisProperties kangAnProperties() {
	 * 	return new RedisProperties();
	 * }
	 * }
	 * 
	 * 再配置routingRedisPool
	 * @SuppressWarnings("unchecked")
	 * @Bean
	 * public RoutingRedisPool<Jedis> routingRedisPool(
	 * 		@Qualifier("cxkgProperties") RedisProperties cxkgProperties, 
	 * 		@Qualifier("kangAnProperties") RedisProperties kangAnProperties) {
	 * 	RoutingRedisPool routingRedisPool = new RoutingRedisPool<>();
	 * 	Map<String, RedisProperties> redisPropertiesMap = new HashMap<>();
	 * 	redisPropertiesMap.put("cxkg", cxkgProperties);
	 * 	redisPropertiesMap.put("kangan", kangAnProperties);
	 * 	return routingRedisPool;
	 * }
	 * 
	 * 最后一步, 为ApplicationContextHolder设置一个applicationContext, 同时对JedisPool进行预热
	 * @Bean
	 * public MethodInvokingBean jedisWarmUp(ApplicationContext applicationContext) {
	 * 	ApplicationContextHolder.setApplicationContext(applicationContext);
	 * 	MethodInvokingBean methodInvokingBean = new MethodInvokingBean();
	 * 	methodInvokingBean.setStaticMethod("com.loserico.cache.redis.JedisUtils.warmUp");
	 * 	return methodInvokingBean;
	 * }
	 * </pre>
	 * @on
	 */
	private static volatile RoutingRedisPool<Jedis> routingRedisPool = null;

	/**
	 * 通过defaultPool或者routingRedisPool获取Resource
	 */
	private static volatile Supplier<Jedis> resourceSupplier = null;

	/**
	 * 默认读取classpath下redis.properties文件
	 */
	private static final PropertyReader propertyReader = new PropertyReader("redis");

	/**
	 * 用户缓存lua脚本的sha
	 */
	private static final ConcurrentHashMap<String, String> shaHashs = new ConcurrentHashMap<>();

	static {
		if (defaultPool == null) {
			synchronized (JedisUtils.class) {
				if (defaultPool == null) {
					defaultPool = JedisPoolFactories.poolFactory().createPool(propertyReader);
				}
				routingRedisPool = ApplicationContextHolder.getBean(RoutingRedisPool.class);

				if (routingRedisPool != null) {
					resourceSupplier = () -> routingRedisPool.determineTargetPool().getResource();
				} else if (defaultPool != null) {
					resourceSupplier = () -> defaultPool.getResource();
				} else {
					throw new NoJedisPoolException(
							"defaultPool and routingRedisPool are both null, please config one.");
				}
			}
		}

	}

	/**
	 * 返回Jedis原生实例 
	 * 
	 * 用完记得调用jedis.close() 归还resource
	 * 
	 * @return Jedis
	 * @on
	 */
	private static Jedis jedis() {
		return resourceSupplier.get();
	}

	/**
	 * key/value 都是字符串的版本
	 * 
	 * @param key
	 * @param value
	 * @return String
	 */
	public static boolean set(String key, String value) {
		return set(toBytes(key), toBytes(value));
	}

	/**
	 * value不是String类型的情况
	 * 
	 * 如果value实现了Serializable接口, 那么用Java的序列化机制
	 * 否则使用Jackson序列化成byte[]
	 * 
	 * @param key
	 * @param value
	 * @return boolean 是否set成功
	 * @on
	 */
	public static boolean set(String key, Object value) {
		return set(toBytes(key), toBytes(value));
	}

	/**
	 * value是List类型, 通过Jackson序列化成json串
	 * 
	 * @param key
	 * @param values
	 * @return boolean 是否set成功
	 */
	public static boolean set(String key, List<?> values) {
		return set(key, toJson(values));
	}

	/**
	 * key不是String类型的情况
	 * 
	 * 如果key实现了Serializable接口, 那么用Java的序列化机制
	 * 否则使用Jackson序列化成byte[]
	 * 
	 * @param key
	 * @param value
	 * @return boolean 是否set成功
	 * @on
	 */
	public static boolean set(Object key, String value) {
		return set(toBytes(key), toBytes(value));
	}

	/**
	 * key/value都不是String类型的情况, 如果key/value实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson序列化成byte[]
	 * 
	 * @param key
	 * @param value
	 * @return boolean 是否set成功
	 */
	public static boolean set(Object key, Object value) {
		return set(toBytes(key), toBytes(value));
	}

	/**
	 * key/value 都是byte[]情况
	 * 
	 * @param key
	 * @param value
	 * @return true表示设置成功
	 */
	public static boolean set(byte[] key, byte[] value) {
		Jedis jedis = jedis();
		try {
			String status = jedis.set(key, value);
			logger.debug("set: {}", status);
			return STATUS_SUCCESS.equals(status);
		} finally {
			jedis.close();
		}
	}

	/**
	 * key/value 都是字符串版本, 同时带过期时间
	 * 
	 * @param key
	 * @param value
	 * @return boolean 表示是否设置成功
	 */
	public static boolean set(String key, String value, long expires, TimeUnit timeUnit) {
		Objects.requireNonNull(timeUnit);
		return set(toBytes(key), toBytes(value), toBytes(expires, timeUnit));
	}

	/**
	 * value不是String类型的情况, 如果value实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson序列化成byte[], 同时带过期时间
	 * 
	 * @param key
	 * @param value
	 * @return String
	 */
	public static boolean set(String key, Object value, long expires, TimeUnit timeUnit) {
		Objects.requireNonNull(timeUnit);
		return set(toBytes(key), toBytes(value), toBytes(expires, timeUnit));
	}

	/**
	 * key不是String类型的情况, 如果key实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson序列化成byte[], 同时设置过期时间
	 * 
	 * @param key
	 * @param value
	 * @return boolean 表示是否设置成功
	 */
	public static boolean set(Object key, String value, long expires, TimeUnit timeUnit) {
		Objects.requireNonNull(key);
		return set(toBytes(key), toBytes(value), toBytes(expires, timeUnit));
	}

	/**
	 * key/value都不是String类型的情况, 如果key/value实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson序列化成byte[]
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean set(Object key, Object value, long expires, TimeUnit timeUnit) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(timeUnit);
		return set(toBytes(key), toBytes(value), toBytes(expires, timeUnit));
	}

	/**
	 * key/value 都是byte[]情况, 同时设置过期时间
	 * 
	 * @param key
	 * @param value
	 * @return true 表示设置成功
	 */
	public static boolean set(byte[] key, byte[] value, byte[] expires) {
		Jedis jedis = jedis();
		try {
			String setExpireSha1 = shaHashs.computeIfAbsent("setExpire.lua", (x) -> {
				logger.info("Load script {}", "setExpire.lua");
				return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/setExpire.lua"));
			});
			long result = (long) jedis.evalsha(toBytes(setExpireSha1), 1, key, value, expires);
			return result == 1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 如果 key 不存在则设置 key=value
	 * 原子操作
	 * @param key
	 * @param value		
	 * @return boolean	是否设置成功
	 * @on
	 */
	public static boolean setnx(String key, String value) {
		Jedis jedis = jedis();
		return 1L == jedis.setnx(toBytes(key), toBytes(value));
	}

	/**
	 * 如果 key 不存在则设置 key=value
	 * 原子操作
	 * @param key
	 * @param value		
	 * @param expires	过期时间
	 * @param unit		过期单位, 毫秒、秒等
	 * @return boolean	是否设置成功
	 * @on
	 */
	public static boolean setnx(String key, Object value) {
		Objects.requireNonNull(key);
		Jedis jedis = jedis();
		return 1L == jedis.setnx(toBytes(key), toBytes(value));
	}

	/**
	 * 如果 key 不存在则设置 key=value
	 * 原子操作
	 * @param key
	 * @param value		
	 * @return boolean	是否设置成功
	 * @on
	 */
	public static boolean setnx(Object key, Object value) {
		Objects.requireNonNull(key);
		Jedis jedis = jedis();
		return 1L == jedis.setnx(toBytes(key), toBytes(value));
	}

	/**
	 * 如果 key 不存在则设置 key=value, 同时设置过期时间
	 * 原子操作
	 * @param key
	 * @param value		
	 * @param expires	过期时间
	 * @param unit		过期单位, 毫秒、秒等
	 * @return boolean	是否设置成功
	 * @on
	 */
	public static boolean setnx(String key, Object value, long expires, TimeUnit timeUnit) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(timeUnit);

		Jedis jedis = jedis();
		try {
			String setnxSha1 = shaHashs.computeIfAbsent("setnx.lua", x -> {
				logger.info("Load script {}", "setnx.lua");
				return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/setnx.lua"));
			});

			long expireInSeconds = timeUnit.toSeconds(expires);
			long result = (long) jedis.evalsha(toBytes(setnxSha1),
					1,
					toBytes(key),
					toBytes(value),
					toBytes(expireInSeconds));

			return result == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * key/value都是字符串
	 * 
	 * @param key
	 * @return String
	 */
	public static String get(String key) {
		return get(toBytes(key));
	}

	/**
	 * value不是字符串的情况, 使用Jackson反序列化
	 * 
	 * @param key
	 * @param clazz
	 * @return T
	 */
	public static <T> T get(String key, Class<T> clazz) {
		return get(toBytes(key), clazz);
	}

	/**
	 * 根据key从缓存中取, 如果取不到对应的value则调用supplier并回填, 回填后默认5分钟过期
	 * 
	 * @param key
	 * @param clazz
	 * @param supplier
	 * @return T
	 */
	public static <T> T get(String key, Class<T> clazz, Supplier<T> supplier) {
		return get(key, clazz, supplier, 5, MINUTES);
	}

	/**
	 * 根据key从缓存中取, 如果取不到对应的value则调用supplier并回填, 同时制定key的过期时间
	 * 
	 * @param key
	 * @param clazz
	 * @param supplier
	 * @return T
	 */
	public static <T> T get(String key, Class<T> clazz, Supplier<T> supplier, long expires, TimeUnit timeUnit) {
		T object = get(key, clazz);
		// 没有命中则调用supplier.get()并回填缓存
		if (object == null) {
			// 成功获取锁才调supplier并回填
			Lock lock = lock(key, expires, timeUnit);
			if (lock.locked()) {
				T result = supplier.get();
				set(key, result, expires, timeUnit);
				lock.unlock();
				return result;
			} else {// 没有获得锁就再从缓存取一遍, 取不到拉倒
				return get(key, clazz);
			}
		}

		return object;
	}

	/**
	 * key不是String类型的情况, 如果key实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson序列化成byte[]
	 * 
	 * @param key
	 * @return String
	 */
	public static String get(Object key) {
		return get(toBytes(key));
	}

	/**
	 * key/value 都不是字符串的情况
	 * 如果key/value 实现了Serializable接口, 那么用Java的序列化机制, 否则使用Jackson反序列化
	 * @param key
	 * @param clazz
	 * @return T
	 * @on
	 */
	public static <T> T get(Object key, Class<T> clazz) {
		return get(toBytes(key), clazz);
	}

	public static String get(byte[] key) {
		Jedis jedis = jedis();
		try {
			byte[] value = jedis.get(key);
			if (value != null && value.length > 0) {
				return new String(value, UTF_8);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static <T> T get(byte[] key, Class<T> clazz) {
		Jedis jedis = jedis();
		try {
			byte[] value = jedis.get(key);
			return toObject(value, clazz);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 获取Long类型的值, 如果这个key不存在这返回null
	 * 
	 * @param key
	 * @return Long
	 */
	public static Long getLong(String key) {
		Jedis jedis = jedis();
		try {
			byte[] value = jedis.get(toBytes(key));
			if (value != null && value.length > 0) {
				return toLong(value);
			}
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * key是字符串, value是一个ArrayList, 通过Jackson序列化反序列化
	 * 
	 * @param key
	 * @param clazz
	 * @return List<T>
	 */
	public static <T> List<T> getList(String key, Class<T> clazz) {
		return getList(toBytes(key), clazz);
	}

	public static <T> List<T> getList(byte[] key, Class<T> clazz) {
		Jedis jedis = jedis();
		try {
			byte[] value = jedis.get(key);
			return toList(value, clazz);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 根据key从缓存中取, 如果取不到对应的value则调用supplier并回填, 默认5分钟过期
	 * 
	 * @param key
	 * @param clazz
	 * @param supplier
	 * @return T
	 */
	public static <T> List<T> getList(String key, Class<T> clazz, Supplier<List<T>> supplier) {
		List<T> object = getList(key, clazz);
		// 没有命中则调用supplier.get()并回填缓存
		if (object == null || object.isEmpty()) {
			// 成功获取锁才调supplier并回填
			Lock lock = lock(key, 1, MINUTES);
			if (lock.locked()) {
				try {
					List<T> result = supplier.get();
					set(key, result, 5, MINUTES);
					return result;
				} finally {
					lock.unlock();
				}
			} else {// 没有获得锁就再从缓存取一遍, 取不到拉倒
				return getList(key, clazz);
			}
		}

		return object;
	}

	/**
	 * 根据key从缓存中取, 如果取不到对应的value则调用supplier并回填, 同时指定key的过期时间
	 * 
	 * @param key
	 * @param clazz
	 * @param supplier
	 * @return List<T>
	 */
	public static <T> List<T> getList(String key, Class<T> clazz, Supplier<List<T>> supplier, long expires,
			TimeUnit timeUnit) {
		Objects.requireNonNull(timeUnit);

		List<T> list = getList(key, clazz);
		// 没有命中则调用supplier.get()并回填缓存
		if (list == null || list.isEmpty()) {
			/*
			 * 成功获取锁才调supplier并回填
			 * 这里锁的时间没关系, 因为拿到锁后执行set操作后就会解锁
			 * @on
			 */
			Lock lock = lock(key, expires, timeUnit);
			if (lock.locked()) {
				try {
					List<T> result = supplier.get();
					if (result != null && !result.isEmpty()) {
						set(key, result, expires, timeUnit);
					}
					return result;
				} finally {
					lock.unlock();
				}
			} else {// 没有获得锁就再从缓存取一遍, 取不到就拉倒
				return getList(key, clazz);
			}
		}

		return list;
	}

	/**
	 * 递增1
	 * 
	 * @param key
	 * @return Long
	 */
	public static Long incr(String key) {
		Jedis jedis = jedis();
		try {
			return jedis.incr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 一次增加size长度
	 * 
	 * @param key
	 * @param value
	 * @return Long
	 */
	public static Long incrBy(String key, long size) {
		Jedis jedis = jedis();
		try {
			return jedis.incrBy(key, size);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * <h4>Redis list 相关操作</h4>
	 * 
	 * List 是简单的字符串列表, 它按插入顺序排序<p>
	 * <ul>
	 * <li>{@code lpush}  向指定的列表左侧插入元素, 返回插入后列表的长度
	 * <li>{@code rpush}  向指定的列表右侧插入元素, 返回插入后列表的长度
	 * <li>{@code llen}   返回指定列表的长度
	 * <li>{@code lrange} 返回指定列表中指定范围的元素值
	 * </ul>
	 * <p>
	 * Copyright: Copyright (c) 2018-08-01 19:08
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static final class LIST {

		/**
		 * lpush 向指定的列表左侧(头部)插入元素, 返回插入后列表的长度
		 * 
		 * @param key
		 * @param values
		 * @return long
		 */
		public static long lpush(String key, String... values) {
			Jedis jedis = jedis();
			try {
				return jedis.lpush(key, values);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * lpush 向指定的列表左侧(头部)插入元素, 返回插入后列表的长度
		 * 
		 * @param key
		 * @param values
		 * @return long
		 */
		public static long lpush(String key, Object... values) {
			Jedis jedis = jedis();
			try {
				return jedis.lpush(toBytes(key), toBytes(values));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * rpush 向指定的列表右侧插入元素, 返回插入后列表的长度
		 * 
		 * @param key
		 * @param values
		 * @return long
		 */
		public static long rpush(String key, String... values) {
			Jedis jedis = jedis();
			try {
				return jedis.rpush(key, values);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * rpush 向指定的列表右侧插入元素, 返回插入后列表的长度
		 * 
		 * @param key
		 * @param values
		 * @return long
		 */
		public static long rpush(String key, Object... values) {
			Jedis jedis = jedis();
			try {
				return jedis.rpush(toBytes(key), toBytes(values));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从左侧(头部)弹出一个元素
		 * 
		 * @param key
		 * @return String
		 */
		public static String lpop(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.lpop(key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从左侧(头部)弹出一个元素
		 * 
		 * @param key
		 * @return T
		 */
		public static <T> T lpop(String key, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				byte[] data = jedis.lpop(toBytes(key));
				return toObject(data, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从左侧(头部)弹出一个元素,阻塞版本
		 * <pre>
		 * BLPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * 
		 * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
		 * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
		 * lists.
		 * <p>
		 * The following is a description of the exact semantic. We describe BLPOP but the two commands
		 * are identical, the only difference is that BLPOP pops the element from the left (head) of the
		 * list, and BRPOP pops from the right (tail).
		 * <p>
		 * <b>Non blocking behavior</b>
		 * <p>
		 * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
		 * element is popped from the head of the list and returned to the caller together with the name
		 * of the key (BLPOP returns a two elements array, the first element is the key, the second the
		 * popped value).
		 * <p>
		 * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
		 * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
		 * guarantees to return an element from the list stored at list2 (since it is the first non empty
		 * list starting from the left).
		 * <p>
		 * <b>Blocking behavior</b>
		 * <p>
		 * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
		 * client performs a LPUSH or an RPUSH operation against one of the lists.
		 * <p>
		 * Once new data is present on one of the lists, the client finally returns with the name of the
		 * key unblocking it and the popped value.
		 * <p>
		 * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
		 * special value if the specified amount of seconds passed without a push operation against at
		 * least one of the specified keys.
		 * <p>
		 * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
		 * block forever.
		 * <p>
		 * <b>Multiple clients blocking for the same keys</b>
		 * <p>
		 * Multiple clients can block for the same key. They are put into a queue, so the first to be
		 * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
		 * <p>
		 * <b>blocking POP inside a MULTI/EXEC transaction</b>
		 * <p>
		 * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
		 * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
		 * transaction).
		 * <p>
		 * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
		 * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
		 * it like if inside MULTI/EXEC the time will flow at infinite speed :)
		 * <p>
		 * Time complexity: O(1)
		 * @see #brpop(int, String...)
		 * @param timeoutSeconds 阻塞多少秒后超时退出
		 * @param keys
		 * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
		 *         unblocking key and the popped value.
		 *         <p>
		 *         When a non-zero timeout is specified, and the BLPOP operation timed out, the return
		 *         value is a nil multi bulk reply. Most client values will return false or nil
		 *         accordingly to the programming language used.
		 * @on
		 */
		public static List<String> blpop(String key, int timeoutSeconds) {
			Jedis jedis = jedis();
			try {
				return jedis.blpop(timeoutSeconds, key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从左侧(头部)弹出一个元素,阻塞版本
		 * <pre>
		 * BLPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里shi list2
		 * 第二个元素表示返回的元素本身 a
		 * 
		 * </pre>
		 * @param keys
		 * @return
		 * @on
		 */
		public static String blpop(String key) {
			Jedis jedis = jedis();
			try {
				List<String> list = jedis.blpop(0, key);
				list.get(1);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
			return null;
		}

		/**
		 * 从左侧(头部)弹出一个元素
		 * 
		 * @param key
		 * @return T
		 */
		public static <T> T blpop(String key, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				byte[][] keys = new byte[1][];
				keys[0] = toBytes(key);
				List<byte[]> elements = jedis.blpop(0, keys);
				return toObject(elements.get(0), clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从左侧(头部)弹出一个元素, 阻塞版本
		 * <pre>
		 * BLPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里shi list2
		 * 第二个元素表示返回的元素本身 a
		 * 
		 * 有元素出队后queueListener会被调用
		 * </pre>
		 * @param keys
		 * @return
		 * @on
		 */
		public static void blpop(String key, QueueListener listener) {
			Jedis jedis = jedis();
			try {
				List<String> results = jedis.blpop(0, key);
				listener.onDeque(results.get(0), results.get(1));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从右侧(尾部)弹出一个元素, 阻塞版本
		 * <pre>
		 * BRPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * 
		 * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
		 * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
		 * lists.
		 * <p>
		 * The following is a description of the exact semantic. We describe BLPOP but the two commands
		 * are identical, the only difference is that BLPOP pops the element from the left (head) of the
		 * list, and BRPOP pops from the right (tail).
		 * <p>
		 * <b>Non blocking behavior</b>
		 * <p>
		 * When BLPOP is called, if at least one of the specified keys contain a non empty list, an
		 * element is popped from the head of the list and returned to the caller together with the name
		 * of the key (BLPOP returns a two elements array, the first element is the key, the second the
		 * popped value).
		 * <p>
		 * Keys are scanned from left to right, so for instance if you issue BLPOP list1 list2 list3 0
		 * against a dataset where list1 does not exist but list2 and list3 contain non empty lists, BLPOP
		 * guarantees to return an element from the list stored at list2 (since it is the first non empty
		 * list starting from the left).
		 * <p>
		 * <b>Blocking behavior</b>
		 * <p>
		 * If none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
		 * client performs a LPUSH or an RPUSH operation against one of the lists.
		 * <p>
		 * Once new data is present on one of the lists, the client finally returns with the name of the
		 * key unblocking it and the popped value.
		 * <p>
		 * When blocking, if a non-zero timeout is specified, the client will unblock returning a nil
		 * special value if the specified amount of seconds passed without a push operation against at
		 * least one of the specified keys.
		 * <p>
		 * The timeout argument is interpreted as an integer value. A timeout of zero means instead to
		 * block forever.
		 * <p>
		 * <b>Multiple clients blocking for the same keys</b>
		 * <p>
		 * Multiple clients can block for the same key. They are put into a queue, so the first to be
		 * served will be the one that started to wait earlier, in a first-blpopping first-served fashion.
		 * <p>
		 * <b>blocking POP inside a MULTI/EXEC transaction</b>
		 * <p>
		 * BLPOP and BRPOP can be used with pipelining (sending multiple commands and reading the replies
		 * in batch), but it does not make sense to use BLPOP or BRPOP inside a MULTI/EXEC block (a Redis
		 * transaction).
		 * <p>
		 * The behavior of BLPOP inside MULTI/EXEC when the list is empty is to return a multi-bulk nil
		 * reply, exactly what happens when the timeout is reached. If you like science fiction, think at
		 * it like if inside MULTI/EXEC the time will flow at infinite speed :)
		 * <p>
		 * Time complexity: O(1)
		 * @see #blpop(int, String...)
		 * @param timeout
		 * @param keys
		 * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
		 *         unblocking key and the popped value.
		 *         <p>
		 *         When a non-zero timeout is specified, and the BLPOP operation timed out, the return
		 *         value is a nil multi bulk reply. Most client values will return false or nil
		 *         accordingly to the programming language used.
		 * @on
		 */
		public static List<String> brpop(int timeout, String... keys) {
			Jedis jedis = jedis();
			try {
				return jedis.brpop(timeout, keys);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从右侧(尾部)弹出一个元素,阻塞版本
		 * <pre>
		 * BRPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * @on
		 */
		public static String brpop(int timeout, String key) {
			Jedis jedis = jedis();
			try {
				List<String> list = jedis.brpop(timeout, key);
				return list.get(1);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从右侧(尾部)弹出一个元素,阻塞版本
		 * <pre>
		 * BRPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * @on
		 */
		public static String brpop(String key) {
			return brpop(0, key);
		}

		/**
		 * 从右侧(尾部)弹出一个元素,阻塞版本
		 * <pre>
		 * BRPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * @on
		 */
		public static <T> T brpop(int timeout, String key, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				List<byte[]> list = jedis.brpop(timeout, toBytes(key));
				byte[] data = list.get(1);
				return toObject(data, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从右侧(尾部)弹出一个元素,阻塞版本
		 * <pre>
		 * BRPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里是 list2; 第二个元素表示返回的元素本身 a
		 * </pre>
		 * @on
		 */
		public static <T> T brpop(String key, Class<T> clazz) {
			return brpop(0, key, clazz);
		}

		/**
		 * 从左侧(头部)弹出一个元素,阻塞版本
		 * <pre>
		 * BLPOP list1 list2 list3 
		 * 假设 list1不存在,  list2有一个元素 a
		 * 那么返回的List包含两个元素,  第一个表示从哪个key返回的, 这里shi list2
		 * 第二个元素表示返回的元素本身 a
		 * 
		 * 有元素出队后queueListener会被调用
		 * </pre>
		 * @param keys
		 * @return
		 * @on
		 */
		public static void brpop(String key, QueueListener listener) {
			Jedis jedis = jedis();
			try {
				List<String> results = jedis.brpop(0, key);
				listener.onDeque(results.get(0), results.get(1));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * llen 返回指定列表的长度
		 * 
		 * @param key
		 * @return
		 */
		public static long llen(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.llen(key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * lrange 返回指定列表中指定范围的元素值
		 * index从0开始,  -1表示最后一个元素
		 * 
		 * @param key
		 * @param start 
		 * @param end
		 * @return List<String>
		 * @on
		 */
		public static List<String> lrange(String key, long start, long end) {
			Jedis jedis = jedis();
			try {
				return jedis.lrange(key, start, end);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * lrange 返回指定列表中指定范围的元素值
		 * index从0开始,  -1表示最后一个元素
		 * 
		 * @param key
		 * @param start 
		 * @param end
		 * @return List<String>
		 * @on
		 */
		public static <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				List<byte[]> list = jedis.lrange(toBytes(key), start, end);
				if (list.isEmpty()) {
					return new ArrayList<>();
				}

				List<T> result = new ArrayList<>();
				for (byte[] bytes : list) {
					result.add(toObject(bytes, clazz));
				}
				return result;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 获取整个List里面的元素
		 * 
		 * @param key
		 * @return List<String>
		 */
		public static List<String> list(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.lrange(key, 0, -1);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 获取整个List里面的元素
		 * 
		 * @param key
		 * @return List<T>
		 */
		public static <T> List<T> list(String key, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				List<byte[]> list = jedis.lrange(toBytes(key), 0, -1);
				if (list.isEmpty()) {
					return new ArrayList<>();
				}

				List<T> result = new ArrayList<>();
				for (byte[] bytes : list) {
					result.add(toObject(bytes, clazz));
				}
				return result;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从列表中删除指定个数的元素
		 * Removes the first count occurrences of elements equal to value from the list stored at key. 
		 * The count argument influences the operation in the following ways:<p>
		 * <ul>                                                         
		 * <li/><code>count > 0</code>: Remove elements equal to value moving from head to tail. 从左边开始删
		 * <li/><code>count < 0</code>: Remove elements equal to value moving from tail to head. 从右边开始删
		 * <li/><code>count = 0</code>: Remove all elements equal to value. 删除所有匹配
		 * </ul>
		 * For example, LREM list -2 "hello" will remove the last two occurrences of "hello" in the list stored at list.
		 * Note that non-existing keys are treated like empty lists, so when key does not exist, the command will always return 0.
		 * 
		 * @param key
		 * @param count
		 * @param value
		 * @return long 删除的元素个数
		 * @on
		 */
		public static long lrem(String key, long count, String value) {
			Jedis jedis = jedis();
			try {
				return jedis.lrem(key, count, value);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

	}

	/**
	 * Redis Set 相关操作
	 * <p>
	 * Copyright: Copyright (c) 2018-08-01 19:09
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static class SET {

		/**
		 * 向Set中添加元素
		 * 
		 * @param key
		 * @param element
		 * @return long 本次添加的元素个数
		 */
		public static long sadd(String key, String element) {
			if (StringUtils.isBlank(element)) {
				return 0;
			}
			Jedis jedis = jedis();
			try {
				return jedis.sadd(key, element);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 向Set中添加元素
		 * 
		 * @param key
		 * @param elements
		 * @return long 本次添加的元素个数
		 */
		public static long sadd(String key, Object... elements) {
			Jedis jedis = jedis();
			try {
				return jedis.sadd(toBytes(key), toBytes(elements));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 在数据库事务正确提交以后向Set中添加元素
		 * 
		 * @param key
		 * @param elements
		 * @return long 本次添加的元素个数
		 */
		public static void saddTransactional(String key, String element) {
			TransactionEvents.instance().afterCommit(() -> {
				sadd(key, element);
			});
		}

		/**
		 * 在数据库事务正确提交以后向Set中添加元素
		 * 
		 * @param key
		 * @param elements
		 * @return long 本次添加的元素个数
		 */
		public static void saddTransactional(String key, Object... elements) {
			TransactionEvents.instance().afterCommit(() -> {
				sadd(key, elements);
			});
		}

		/**
		 * 获取Set所有元素
		 * 
		 * @param key
		 * @return Set<String>
		 */
		public Set<String> smembers(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.smembers(key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 获取Set所有元素
		 * 
		 * @param key
		 * @return Set<String>
		 */
		public <T> Set<T> smembers(String key, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				Set<byte[]> byteSet = jedis.smembers(toBytes(key));
				Set<T> resultSet = new HashSet<>();
				for (byte[] data : byteSet) {
					resultSet.add(toObject(data, clazz));
				}
				return resultSet;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 检查 value 是否在 Set中
		 * 
		 * @param key
		 * @param value
		 * @return boolean
		 */
		public static boolean sismember(String key, Object element) {
			Jedis jedis = jedis();
			try {
				return jedis.sismember(toBytes(key), toBytes(element));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 返回Set中元素个数
		 * 
		 * @param key
		 * @return long
		 */
		public static long scard(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.scard(key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 从Set中移除元素, 返回实际移除的元素个数
		 * 
		 * @param key
		 * @param elements
		 * @return long
		 */
		public static long srem(String key, Object... elements) {
			Jedis jedis = jedis();
			try {
				return jedis.srem(toBytes(key), toBytes(elements));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

	}

	/**
	 * Redis Sorted Set 相关操作
	 * <p>
	 * Copyright: Copyright (c) 2018-07-27 21:28
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static final class ZSET {

		/**
		 * 获取member的score
		 * 
		 * @param key
		 * @param member
		 * @return double
		 */
		public static double zscore(String key, String member) {
			Jedis jedis = jedis();
			try {
				return jedis.zscore(key, member);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

	/**
	 * Redis HASH 相关操作
	 * <p>
	 * Copyright: Copyright (c) 2018-08-07 21:01
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static final class HASH {

		// hash每个field的过期时间记录在key为 jedis_utils:__timeout__set:key 的zset中
		private static final String HASH_EXPIRE_ZSET_PREFIX = "jedis_utils:__timeout__set";

		/**
		 * key 是Map的名字
		 * field 是Map里面的field
		 * <ul>返回
		 * <li>0 表示更新了map中的field
		 * <li>1 表示在map上新增了一个field
		 * @param key
		 * @param field
		 * @param value
		 * @return int
		 * @on
		 */
		public static int hset(String key, Object field, Object value) {
			Jedis jedis = jedis();
			try {
				Long result = (Long) jedis.hset(toBytes(key), toBytes(field), toBytes(value));
				return result.intValue();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 设置Hash某个field值,同时指定其过期时间,单位秒
		 * <ul>返回
		 * <li>UPDATED(0) 表示更新了map中的field
		 * <li>INSERTED(1) 表示在map上新增了一个field
		 * </ul>
		 * @param key
		 * @param field
		 * @param value
		 * @param ttl field过期时间,秒
		 * @return HSetStatus 
		 * @on
		 */
		public static HSet hset(String key, Object field, Object value, int ttl) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.info("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Long result = (Long) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key), // hash key
						toBytes(zsetKey), // zset key
						toBytes("hset"), // 调用的lua function名字
						toBytes(field),
						toBytes(value),
						toBytes(ttl));
				return result.intValue() == 0 ? UPDATED : INSERTED;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 都是字符串的情况
		 * 
		 * @param key
		 * @param hash
		 */
		public static boolean hmset(String key, Map<String, String> hash) {
			Jedis jedis = jedis();
			try {
				String result = jedis.hmset(key, hash);
				return STATUS_SUCCESS.equalsIgnoreCase(result);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 将整个Map添加到Redis中, 返回AtomicLongArray
		 * 第一个元素表示更新的field数量
		 * 第二个元素表示新增的field数量
		 * @param key
		 * @param map
		 * @return AtomicLongArray
		 * @on
		 */
		public static <K, V> AtomicLongArray hmsetGeneric(String key, Map<K, V> map) {
			Jedis jedis = jedis();
			AtomicLongArray statistic = new AtomicLongArray(2);
			try {
				map.entrySet().forEach((entry) -> {
					Long type = jedis.hset(toBytes(key), toBytes(entry.getKey()), toBytes(entry.getValue()));
					if (type == 0) {
						statistic.incrementAndGet(0);
					} else {
						statistic.incrementAndGet(1);
					}
				});
				return statistic;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * field 和 value 都是字符串的情况调这个接口
		 * 
		 * @param key
		 * @param field
		 * @return String
		 */

		public static String hget(String key, String field) {
			if (key == null || "".equals(key.trim())) {
				return null;
			}
			if (field == null || "".equals(field.trim())) {
				return null;
			}

			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				byte[] data = (byte[]) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key), // hash key
						toBytes(zsetKey), // zset key
						toBytes("hget"), // 调用的lua function名字
						toBytes(field));
				return JedisUtils.toString(data);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * field是任意对象, 但是value是字符串的情况调这个
		 * 
		 * @param key
		 * @param field
		 * @return String
		 */
		public static String hget(String key, Object field) {
			Jedis jedis = jedis();
			try {
				if (field == null) {
					return null;
				}
				return hget(key, field, String.class);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * key 是 String
		 * field 是任意类型
		 * javaType 可以通过 TypeUtils 获取
		 * @param key
		 * @param clazzValue
		 * @return T
		 * @on
		 */
		public static <T> T hget(String key, Object field, JavaType javaType) {
			Jedis jedis = jedis();
			try {
				byte[] data = jedis.hget(toBytes(key), toBytes(field));
				;
				try {
					return JacksonUtils.objectMapper().readValue(JedisUtils.toString(data), javaType);
				} catch (IOException e) {
					logger.error("将value转成集合类型时失败", e);
					throw new JedisValueOperationException(e);
				}
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * field是任意对象, value是Class<T>指定的类型
		 * 
		 * @param key
		 * @param field
		 * @param clazz
		 * @return T
		 */
		public static <T> T hget(String key, Object field, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				if (field == null) {
					return null;
				}
				byte[] data = jedis.hget(toBytes(key), toBytes(field));
				return toObject(data, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * Map的field对应的值是一个List的情况
		 * 
		 * @param key
		 * @param field
		 * @param clazz
		 * @return List<T>
		 */
		public static <T> List<T> hgetList(String key, Object field, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				if (field == null) {
					return null;
				}
				byte[] data = jedis.hget(toBytes(key), toBytes(field));
				return toList(data, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * key/value 都是字符串
		 * 
		 * @param key
		 * @return Map<String, String>
		 */
		public static Map<String, String> hgetAll(String key) {
			Jedis jedis = jedis();
			try {
				return jedis.hgetAll(key);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * key 是 String
		 * value 是 List<V> 
		 * CollectionType 可以通过 TypeUtils.listType(Class<T>) 获取
		 * @param key
		 * @param clazzValue
		 * @return Map<String, List<V>>
		 * @on
		 */
		public static <V> Map<String, V> hgetAll(String key, JavaType javaType) {
			Jedis jedis = jedis();
			try {
				Map<byte[], byte[]> map = jedis.hgetAll(toBytes(key));
				return map.entrySet().stream()
						.collect(toMap(
								(entry) -> JedisUtils.toString(entry.getKey()),
								(entry) -> {
									try {
										return JacksonUtils.objectMapper()
												.readValue(JedisUtils.toString(entry.getValue()), javaType);
									} catch (IOException e) {
										logger.error("将value转成集合类型时失败", e);
										throw new JedisValueOperationException(e);
									}
								}));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * key/value 是任意确定的类型, 如value可以都是Long, 但不可以同时Long、LocalDate类型
		 * 
		 * @param key
		 * @param clazzKey
		 * @param clazzValue
		 * @return Map<K, V>
		 */
		public static <K, V> Map<K, V> hgetAll(String key, Class<K> clazzKey, Class<V> clazzValue) {
			return hgetAll(toBytes(key), clazzKey, clazzValue);
		}

		/**
		 * key/value 是任意确定的类型, 如value可以都是Long, 但不可以同时Long、LocalDate类型
		 * 
		 * @param key
		 * @param clazzKey
		 * @param clazzValue
		 * @return Map<K, List<V>>
		 */
		public static <K, V> Map<K, V> hgetAll(String key, Class<K> clazzKey, JavaType javaType) {
			Jedis jedis = jedis();
			try {
				Map<byte[], byte[]> map = jedis.hgetAll(toBytes(key));
				return map.entrySet().stream()
						.collect(toMap(
								(entry) -> toObject(entry.getKey(), clazzKey),
								(entry) -> {
									try {
										return JacksonUtils.objectMapper()
												.readValue(JedisUtils.toString(entry.getValue()), javaType);
									} catch (IOException e) {
										logger.error("将value转成集合类型时失败", e);
										throw new JedisValueOperationException(e);
									}
								}));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * key/value 是任意确定的类型, 如value可以都是Long, 但不可以同时Long、LocalDate类型
		 * 
		 * @param key
		 * @param clazzKey
		 * @param clazzValue
		 * @return Map<K, V>
		 */
		public static <K, V> Map<K, V> hgetAll(byte[] key, Class<K> clazzKey, Class<V> clazzValue) {
			Jedis jedis = jedis();
			try {
				Map<byte[], byte[]> map = jedis.hgetAll(key);
				return map.entrySet().stream()
						.collect(toMap(
								(entry) -> toObject(entry.getKey(), clazzKey),
								(entry) -> toObject(entry.getValue(), clazzValue)));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 根据指定的key和要获取的field列表, 找到对应的value, 最后以Map形式返回
		 * 
		 * @param key
		 * @param fields
		 * @return Map<String, String>
		 */
		public static Map<String, String> hmget(String key, String... fields) {
			Jedis jedis = jedis();
			try {
				List<String> values = jedis.hmget(key, fields);
				Map<String, String> resultMap = new HashMap<>();
				for (int i = 0; i < fields.length; i++) {
					String field = fields[i];
					resultMap.put(field, values.get(i));
				}
				return resultMap;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 根据指定的key和要获取的field列表, 找到对应的value, 最后以Map形式返回
		 * 
		 * @param key
		 * @param fields
		 * @return Map<String, String>
		 */
		public static <K, V> Map<K, V> hmget(String key, List<K> fields, Class<V> clazzValue) {
			Jedis jedis = jedis();
			try {
				List<byte[]> values = jedis.hmget(toBytes(key), toBytes(fields));
				Map<K, V> resultMap = new HashMap<>();
				for (int i = 0; i < fields.size(); i++) {
					K field = fields.get(i);
					resultMap.put(field, toObject(values.get(i), clazzValue));
				}
				return resultMap;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 查看哈希表 key 中, 指定的field字段是否存在。
		 * 
		 * @param key
		 * @param field
		 * @return
		 */
		public static boolean hexists(String key, String field) {
			Jedis jedis = jedis();
			try {
				return jedis.hexists(key, field);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 查看哈希表 key 中, 指定的field字段是否存在。
		 * 
		 * @param key
		 * @param field
		 * @return
		 */
		public static boolean hexists(String key, Object field) {
			Jedis jedis = jedis();
			try {
				return jedis.hexists(toBytes(key), toBytes(field));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 删除Hash的某个field
		 * 
		 * @param key
		 * @param field
		 * @return int 删除的field数量
		 */
		public static int hdel(String key, Object field) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.info("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Long result = (Long) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key), // hash key
						toBytes(zsetKey), // zset key
						toBytes("hdel"), // 调用的lua function名字
						toBytes(field));
				return result.intValue();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 返回 hash field 的剩余存活时间(秒)
		 * <ul>
		 * <li>返回KEY_NOT_EXIST(-3) 表示HASH key不存在
		 * <li>返回FIELD_NOT_EXIST(-2) 表示字段不存在
		 * <li>返回NO_EXPIRE(-1) 表示字段存在但是没有过期时间
		 * <li>返回剩余的ttl
		 * </ul>
		 * @param key 	hash的key
		 * @param field	hash的field
		 * @return long	
		 * @on
		 */
		public static TTL ttl(String key, String field) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Long result = (Long) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key),
						toBytes(zsetKey),
						toBytes("ttl"),
						toBytes(field));
				int code = result.intValue();
				switch (code) {
				case -3:
					return TTL.KEY_NOT_EXIST;
				case -2:
					return TTL.FIELD_NOT_EXIST;
				case -1:
					return TTL.NO_EXPIRE;
				default:
					TTL ttlStatus = TTL.TTL;
					ttlStatus.setTtl(code);
					return ttlStatus;
				}
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 设置hash field过期时间
		 * <ul>
		 * <li>如果设置成功返回1
		 * <li>如果key或者field不存在则返回0
		 * <ul/>
		 * 
		 * @param key
		 * @param field
		 * @param ttl
		 * @return
		 */
		public static int expire(String key, String field, int ttl) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Long result = (Long) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key),
						toBytes(zsetKey),
						toBytes("expire"),
						toBytes(field),
						toBytes(ttl));
				return result.intValue();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 移除hash field过期时间 
		 * <ul> 
		 * <li>返回1表示成功移除过期时间
		 * <li>返回0表示key或field不存在或者没有设置过期时间
		 * 
		 * @param key
		 * @param field
		 * @on
		 * @return
		 */
		public static int persist(String key, String field) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Long result = (Long) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key),
						toBytes(zsetKey),
						toBytes("persist"),
						toBytes(field));
				return result.intValue();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 拿redis服务器当前时间戳
		 * 
		 * @return
		 */
		public static long time() {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				long milis = (long) jedis.evalsha(toBytes(hashSha), 0, toBytes("time"));
				return milis;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 拿过期的field
		 * 
		 * @return
		 */
		public static List<String> expiredFields(String key) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				byte[] bytes = (byte[]) jedis.evalsha(toBytes(hashSha),
						1,
						toBytes(zsetKey),
						toBytes("expiredFields"));
				String json = JedisUtils.toString(bytes);
				return JacksonUtils.toList(json, String.class);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 调试用
		 */
		public static void testPurpose(String key, String field) {
			Jedis jedis = jedis();
			try {
				String hashSha = shaHashs.computeIfAbsent("hash.lua", x -> {
					logger.debug("Load script {}", "hash.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/hash.lua"));
				});

				String zsetKey = joinKey(HASH_EXPIRE_ZSET_PREFIX, key);
				Object data = (Object) jedis.evalsha(toBytes(hashSha),
						2,
						toBytes(key),
						toBytes(zsetKey),
						toBytes("ttl"),
						toBytes(field));
				if (!PrimitiveUtils.isByteArray(data)) {
					System.out.println(data);
				} else {
					System.out.println(JedisUtils.toString((byte[]) data));
				}
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
	}

	/**
	 * 基于Redis的登录/认证系统
	 * 执行登录, 登出, token过期清理与过期token通知
	 * 还有单点登录下线通知
	 * <p>
	 * Copyright: Copyright (c) 2018-07-27 16:53
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static final class AUTH {
		/**
		 * 根据用户名获取token
		 */
		public static final String AUTH_USERNAME_TOKEN_HASH = "auth:username:token";

		/**
		 * 根据token获取用户名
		 */
		public static final String AUTH_TOKEN_USERNAME_HASH = "auth:token:username";

		/**
		 * 根据token获取userdetails, Spring Security的UserDetails对象
		 */
		public static final String AUTH_TOKEN_USERDETAILS_HASH = "auth:token:userdetails";

		/**
		 * 根据token获取authorities, Spring Security的GrantedAuthority对象
		 */
		public static final String AUTH_TOKEN_AUTHORITIES_HASH = "auth:token:authorities";

		/**
		 * 根据token获取loginInfo, 这是调用login时传入的额外信息, 如设备ID, IP地址等等
		 */
		public static final String AUTH_TOKEN_LOGIN_INFO_HASH = "auth:token:login:info";

		/**
		 * 一个zset, value是token, score是token的过期时间
		 */
		public static final String AUTH_TOKEN_TTL_ZSET = "auth:token:ttl:zset";

		/**
		 * token过期后, 会publish一条消息到这个channel, 消息体是JSON格式的Map
		 * key是过期的token, value是LoginInfo对象 
		 * 格式: {token:loginInfo, token:loginInfo, ...}
		 * @on
		 */
		public static final String AUTH_TOKEN_EXPIRE_CHANNEL = "auth:token:expired";

		/**
		 * 是否自动刷新token
		 * 
		 * 如果token快要过期了, 此时被刷新, 则表示过期时间被重置了.
		 */
		private static boolean autoRefresh = propertyReader.getBoolean("redis.auth.auto-refresh", true);

		/**
		 * 执行登录操作, 返回登录成功与否, 如果同一账号已经在别处登录, 先对其执行登出, 并将之前的登录信息返回
		 * 
		 * @param username    用户名
		 * @param token       token
		 * @param expires     过期时间
		 * @param timeUnit    单位
		 * @param userdetails Spring Security的UserDetails对象
		 * @param authorities Spring Security的GrantedAuthority对象
		 * @param loginInfo   额外的登录信息
		 * @return LoginResult<T>
		 * @on
		 */
		public static <T> LoginResult<T> login(String username,
				String token,
				long expires, TimeUnit timeUnit,
				Object userdetails,
				List<?> authorities,
				T loginInfo) {
			Objects.requireNonNull(timeUnit);

			Jedis jedis = jedis();
			try {
				String setnxSha1 = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				byte[] result = (byte[]) jedis.evalsha(toBytes(setnxSha1),
						0,
						toBytes("login"),
						toBytes(username),
						toBytes(token),
						toBytes((expires == -1 ? expires : timeUnit.toMillis(expires))),
						toBytes(toJson(userdetails)),
						toBytes(toJson(authorities)),
						toBytes(loginInfo));

				String resultJson = JedisUtils.toString(result);
				T lastLoginInfo = JsonPathUtils.readNode(resultJson, "$.lastLoginInfo");
				boolean success = JsonPathUtils.readNode(resultJson, "$.success");
				LoginResult<T> loginResult = new LoginResult<>(success, lastLoginInfo);
				return (LoginResult<T>) loginResult;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 执行登出操作 返回true表示登出成功 返回false表示该token不存在或者已经登出
		 * 
		 * @param token
		 * @return boolean
		 */
		public static boolean logout(String token) {
			Jedis jedis = jedis();
			try {
				String setnxSha1 = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				byte[] result = (byte[]) jedis.evalsha(toBytes(setnxSha1),
						0,
						toBytes("logout"),
						toBytes(token));
				return Boolean.valueOf(JedisUtils.toString(result));
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 清除过期的token 
		 * 
		 * 没有token过期 返回emptyMap 
		 * token过期, 返回map的key是token, value是LoginInfo
		 * 
		 * @return
		 * @on
		 */
		public static <T> Map<String, T> clearExpired() {
			logger.info("开始清理过期token");
			Jedis jedis = jedis();
			try {
				String clearExpiredSha1 = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				byte[] result = (byte[]) jedis.evalsha(toBytes(clearExpiredSha1), 0, toBytes("clearExpired"));
				String resultJson = JedisUtils.toString(result);
				return resultJson == null ? Collections.emptyMap() : JacksonUtils.toMap(resultJson);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * <pre>
		 * 检查token是否存在并且未过期
		 * 如果通过, 返回token对应的用户名
		 * 否则返回null
		 * </pre>
		 * @param token
		 * @on
		 */
		public static String auth(String token) {
			Objects.requireNonNull(token, "token cannot be null");
			Jedis jedis = jedis();
			try {
				String authSha = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				byte[] result = (byte[]) jedis.evalsha(toBytes(authSha),
						0,
						toBytes("auth"),
						toBytes(token),
						toBytes(autoRefresh));
				return JedisUtils.toString(result);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
		
		/**
		 * 返回0表示这个username没有登录
		 * 返回-1表示usernameTtl检查的时候发现这个用户登录已经过期, 同时会清理其登录信息
		 * 返回这个username对应的token剩余多少秒过期
		 * @param username
		 * @return
		 * @on
		 */
		public static Long usernameTtl(String username) {
			Objects.requireNonNull(username, "username cannot be null");
			Jedis jedis = jedis();
			try {
				String authSha = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				Long ttlInSeconds = (Long) jedis.evalsha(toBytes(authSha),
						0,
						toBytes("usernameTtl"),
						toBytes(username));
				logger.info("{} remain in {} seconds to expire", username, ttlInSeconds);
				return ttlInSeconds;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * <pre>
		 * 检查指定用户是否已登录并且token未过期
		 * 如果通过, 返回对应的token
		 * 否则返回null
		 * </pre>
		 * @param token
		 * @on
		 */
		public static String isLogined(String username) {
			Jedis jedis = jedis();
			try {
				String authSha = shaHashs.computeIfAbsent("spring-security-auth.lua", x -> {
					logger.info("Load script {}", "spring-security-auth.lua");
					return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/spring-security-auth.lua"));
				});

				byte[] result = (byte[]) jedis.evalsha(toBytes(authSha), 0, toBytes("isLogined"), toBytes(username),
						toBytes(autoRefresh));
				return JedisUtils.toString(result);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 根据token获取UserDetails对象
		 * 
		 * @param token
		 * @param clazz
		 * @return T
		 */
		public static <T> T userDetails(String token, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				String userdetails = JedisUtils.HASH.hget(AUTH_TOKEN_USERDETAILS_HASH, token);
				return JacksonUtils.toObject(userdetails, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 根据token获取登录时提供的authorities
		 * 
		 * @param token
		 * @param clazz
		 * @return List<T>
		 */
		public static <T> List<T> authorities(String token, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				String authorities = JedisUtils.HASH.hget(AUTH_TOKEN_AUTHORITIES_HASH, token);
				return JacksonUtils.toList(authorities, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 返回登录时提供的loginInfo
		 * 
		 * @param token
		 * @param clazz
		 * @return T
		 */
		public static <T> T loginInfo(String token, Class<T> clazz) {
			Jedis jedis = jedis();
			try {
				String loginInfo = JedisUtils.HASH.hget(AUTH_TOKEN_LOGIN_INFO_HASH, token);
				return JacksonUtils.toObject(loginInfo, clazz);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * 根据accessToken获取用户名
		 * 
		 * @param token
		 * @return
		 */
		public static String username(String token) {
			Jedis jedis = jedis();
			try {
				return JedisUtils.HASH.hget(AUTH_TOKEN_USERNAME_HASH, token);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		/**
		 * Token过期后接受通知
		 * 
		 * 参数是一个Map<String, T>, T 即调用login()时传入的loginInfo
		 * 
		 * @param consumer
		 */
		public static <T> void onTokenExpire(Consumer<Map<String, T>> consumer) {
			subscribe(AUTH.AUTH_TOKEN_EXPIRE_CHANNEL, (channel, message) -> {
				logger.info("频道{} 收到消息 {}", channel, message);
				Map<String, T> result = JacksonUtils.toMap(message);
				consumer.accept(result);
			});
		}

		/**
		 * 定期自动清理token
		 */
		static {
			int period = JedisUtils.propertyReader.getInt("redis.auth.clear-expired.period", 1); // 默认1分钟执行一次
			if (-1 != period) { // -1表示不执行清理
				ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
				scheduledExecutorService.scheduleAtFixedRate(() -> clearExpired(),
						1,
						period,
						TimeUnit.MINUTES);
			}
		}
	}

	/**
	 * 对给定时间内(expire 秒数)访问次数不超过 count 次
	 * 返回     true  表示没有超过
	 * 	    false 表示超过
	 * @param key
	 * @param expire
	 * @param count
	 * @return boolean
	 * @on
	 */
	public static boolean rateLimit(String key, int expire, int count) {
		Jedis jedis = jedis();
		try {
			String rateLimitSha1 = shaHashs.computeIfAbsent("rateLimit.lua",
					(x) -> {
						logger.info("Load script {}", "rateLimit.lua");
						return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/rateLimit.lua"));
					});
			long result = (long) jedis.evalsha(toBytes(rateLimitSha1),
					1,
					toBytes(join(":", "rate", "limit", key)),
					toBytes(expire), toBytes(count));
			return result == 1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 指定key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		Jedis jedis = jedis();
		try {
			boolean exists = jedis.exists(key);
			return exists;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static boolean exists(Object key) {
		Jedis jedis = jedis();
		try {
			boolean exists = false;
			if (Serializable.class.isInstance(key)) {
				exists = jedis.exists(toBytes(key));
			} else {
				exists = jedis.exists(JacksonUtils.toBytes(key));
			}
			return exists;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间, 单位秒
	 * 
	 * @param key
	 * @param timeout
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expire(String key, int timeout) {
		Jedis jedis = jedis();
		try {
			return jedis.expire(key, timeout) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间, 单位秒
	 * 
	 * @param key
	 * @param timeout
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expire(Object key, int timeout) {
		Jedis jedis = jedis();
		try {
			return jedis.expire(toBytes(key), timeout) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间及单位
	 * 
	 * @param key
	 * @param timeout
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expire(String key, int timeout, TimeUnit timeUnit) {
		Jedis jedis = jedis();
		try {
			return jedis.expire(key, toSeconds(timeout, timeUnit)) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间及单位
	 * 
	 * @param key
	 * @param timeout
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expire(Object key, int timeout, TimeUnit timeUnit) {
		Jedis jedis = jedis();
		try {
			return jedis.expire(toBytes(key), toSeconds(timeout, timeUnit)) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间及单位
	 * 
	 * @param key
	 * @param unixTime UNIX时间戳
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expireAt(String key, long unixTime) {
		Jedis jedis = jedis();
		try {
			return jedis.expireAt(key, unixTime) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置过期时间及单位
	 * 
	 * @param key
	 * @param unixTime UNIX时间戳
	 * @return boolean 是否成功设置了过期时间
	 */
	public static boolean expireAt(Object key, long unixTime) {
		Jedis jedis = jedis();
		try {
			return jedis.expireAt(toBytes(key), unixTime) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 清除key的过期时间
	 * 
	 * @param key
	 * @return
	 */
	public static boolean persist(String key) {
		Jedis jedis = jedis();
		try {
			return jedis.persist(key) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 清除key的过期时间
	 * 
	 * @param key
	 * @return
	 */
	public static boolean persist(Object key) {
		Jedis jedis = jedis();
		try {
			return jedis.persist(toBytes(key)) == 1;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 返回key的过期时间, 单位秒。 如果key没有设置过期时间, 返回 -1 如果key不存在, 返回 -2
	 * 
	 * @param key
	 * @return long 返回key的过期时间, 单位秒, -1 表示没有过期时间 -2 表示可以不存在
	 */
	public static long ttl(String key) {
		Jedis jedis = jedis();
		try {
			return jedis.ttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 返回key的过期时间, 单位秒。 如果key没有设置过期时间, 返回 -1 如果key不存在, 返回 -2
	 * 
	 * @param key
	 * @return long 返回key的过期时间, 单位秒, -1 表示没有过期时间 -2 表示可以不存在
	 */
	public static long ttl(Object key) {
		Jedis jedis = jedis();
		try {
			return jedis.ttl(toBytes(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void del(String key) {
		Jedis jedis = jedis();
		try {
			jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void del(Object key) {
		Jedis jedis = jedis();
		try {
			jedis.del(toBytes(key));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T eval(String script) {
		Jedis jedis = jedis();
		try {
			return (T) jedis.eval(script);
		} finally {
			jedis.close();
		}
	}

	/**
	 * keyCount是1则params中第一个是key, 余下的是value
	 * 
	 * @param script
	 * @param keyCount
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T eval(String script, int keyCount, String... params) {
		Jedis jedis = jedis();
		try {
			return (T) jedis.eval(script, keyCount, params);
		} finally {
			jedis.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T evalsha(String sha) {
		Jedis jedis = jedis();
		try {
			return (T) jedis.evalsha(sha);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 发布消息, 返回接收到消息的订阅者数量
	 * 
	 * @param channel
	 * @param message
	 * @return long
	 */
	public static long publish(String channel, Object message) {
		return publish(toBytes(channel), toBytes(message));
	}

	/**
	 * 发布消息, 返回接收到消息的订阅者数量
	 * 
	 * @param channel
	 * @param message
	 * @return long
	 */
	public static long publish(String channel, String message) {
		return publish(toBytes(channel), toBytes(message));
	}

	/**
	 * 发布消息, 返回接收到消息的订阅者数量
	 * 
	 * @param channel
	 * @param message
	 * @return long
	 */
	public static long publish(byte[] channel, byte[] message) {
		Jedis jedis = jedis();
		try {
			return jedis.publish(channel, message);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 异步方式订阅频道, 收到消息后回调 messageListener
	 * 
	 * @param chnannel
	 * @param messageListener
	 * @return JedisPubSub 用于取消订阅
	 */
	public static JedisPubSub subscribe(String chnannel, MessageListener messageListener) {
		Jedis jedis = jedis();
		JedisPubSub jedisPubSub = new JedisPubSub() {

			@Override
			public void onMessage(String channel, String message) {
				messageListener.onMessage(channel, message);
			}
		};
		ConcurrentTemplate.execute(() -> {
			try {
				jedis.subscribe(jedisPubSub, chnannel);
			} catch (JedisDataException e) {
				logger.error("订阅出错了？", e);
				if (jedis != null) {
					jedis.close();
				}
			}
		});
		return jedisPubSub;
	}

	/**
	 * 异步方式订阅频道, 收到消息后回调 messageListener
	 * 
	 * @param chnannel
	 * @param messageListener
	 * @return JedisPubSub 用于取消订阅
	 */
	public static JedisPubSub subscribe(String chnannel, MessageListener messageListener,
			UnSubsccribeListener unSubsccribeListener) {
		Jedis jedis = jedis();
		try {
			JedisPubSub jedisPubSub = new JedisPubSub() {

				@Override
				public void onMessage(String channel, String message) {
					messageListener.onMessage(channel, message);
				}

				@Override
				public void onUnsubscribe(String channel, int subscribedChannels) {
					unSubsccribeListener.onUnsubscribe(channel, subscribedChannels);
				}

			};
			ConcurrentTemplate.execute(() -> {
				jedis.subscribe(jedisPubSub, chnannel);
			});
			return jedisPubSub;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 取消订阅
	 * 
	 * @param jedisPubSub
	 * @param channel
	 */
	public static void unsubscribe(JedisPubSub jedisPubSub, String channel) {
		Jedis jedis = jedis();
		try {
			jedisPubSub.unsubscribe(channel);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * <pre>
	 * <b>非公平锁</b></p>
	 * Redis 下获取分布式锁
	 * 不会等待锁, 拿不到直接返回
	 * 
	 * 为了确保分布式锁可用, 我们至少要确保锁的实现同时满足以下四个条件：
	 * 
	 * 1 互斥性			在任意时刻, 只有一个客户端能持有锁。
	 * 2 不会发生死锁		即使有一个客户端在持有锁的期间崩溃而没有主动解锁, 也能保证后续其他客户端能加锁。
	 * 3 具有容错性		只要大部分的Redis节点正常运行, 客户端就可以加锁和解锁。
	 * 4 解铃还须系铃人		加锁和解锁必须是同一个客户端, 客户端自己不能把别人加的锁给解了。
	 * 
	 * 第一个为key		我们使用key来当锁, 因为key是唯一的。
	 * 第二个为value		我们传的是requestId, 很多童鞋可能不明白, 有key作为锁不就够了吗, 为什么还要用到value？原因就是我们在上面讲到可靠性时, 分布式锁要满足第四个条件解铃还须系铃人, 
	 * 			通过给value赋值为requestId, 我们就知道这把锁是哪个请求加的了, 在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成。
	 * 第三个为nxxx		这个参数我们填的是NX, 意思是SET IF NOT EXIST, 即当key不存在时, 我们进行set操作；若key已经存在, 则不做任何操作；
	 * 第四个为expx		这个参数我们传的是PX, 意思是我们要给这个key加一个过期的设置, 具体时间由第五个参数决定。
	 * 第五个为time		与第四个参数相呼应, 代表key的过期时间。
	 * 
	 * Safety and Liveness guarantees
	 * 
	 * We are going to model our design with just three properties that, from our point of view, 
	 * are the minimum guarantees needed to use distributed locks in an effective way.
	 * 
	 * Safety property: 	Mutual exclusion. At any given moment, only one client can hold a lock.
	 * Liveness property A: Deadlock free. 
	 * 						Eventually it is always possible to acquire a lock, even if the client that locked a resource crashes or gets partitioned.
	 * Liveness property B: Fault tolerance. 
	 * 						As long as the majority of Redis nodes are up, clients are able to acquire and release locks.
	 * 
	 * 但是如果再考虑下面的场景：
	 * 	- 如果Redis是Master/Slave模式
	 * 	- 客户端A对资源A加锁, 加锁成功后Master突然挂掉但此时Master还未来得及同步刚刚加的锁到Salve
	 * 	- Slave晋升为Master
	 * 	- 客户端B对资源A加锁成功, 但实际上客户端A已经加锁成功了
	 * 
	 * Superficially this works well, but there is a problem: this is a single point of failure in our architecture. 
	 * What happens if the Redis master goes down? Well, let’s add a slave! And use it if the master is unavailable. 
	 * This is unfortunately not viable. By doing so we can’t implement our safety property of mutual exclusion, because Redis replication is asynchronous.
	 * 
	 * There is an obvious race condition with this model:
	 * 
	 * Client A acquires the lock in the master.
	 * The master crashes before the write to the key is transmitted to the slave.
	 * The slave gets promoted to master.
	 * Client B acquires the lock to the same resource A already holds a lock for. SAFETY VIOLATION!
	 * 
	 * Sometimes it is perfectly fine that under special circumstances, like during a failure, multiple clients can hold the lock at the same time. 
	 * If this is the case, you can use your replication based solution. </pre>
	 * 
	 * @param key 锁的名字
	 * @param timestamp key过期时间, 单位秒
	 * @return String token 返回null表示没有获取到锁,  返回一个token表示成功获取锁, 解锁的时候用这个token来解锁
	 * @on
	 */
	public static Lock lock(String key, long leaseTime) {
		String requestId = UUID.randomUUID().toString().replaceAll("-", "");
		String lockKey = nonBlockingLockKey(key);
		boolean success = setnx(lockKey, requestId, leaseTime, TimeUnit.SECONDS);
		return new NonBlockingLock(key, requestId, success); // 这里传原始的key
	}

	/**
	 * 对传入的原始key, 加上前缀/后缀, 组合成最终锁使用的key, 然后尝试对这个最终的key加锁
	 * 
	 * @param key
	 * @param leaseTime
	 * @param timeUnit
	 * @return Lock
	 */
	public static Lock lock(String key, long leaseTime, TimeUnit timeUnit) {
		String requestId = UUID.randomUUID().toString().replaceAll("-", "");
		String lockKey = nonBlockingLockKey(key);
		boolean success = setnx(lockKey, requestId, leaseTime, timeUnit);
		return new NonBlockingLock(key, requestId, success); // 这里传原始的key
	}

	/**
	 * 释放分布式锁
	 * 
	 * token 是成功加锁后返回的, 这里回传是为了确定解锁的是自己加的锁, 否则会把别人加的锁给解锁了。
	 * 
	 * Lua 脚本:
	 * 	if redis.call("get",KEYS[1]) == ARGV[1] then
	 * 		return redis.call("del",KEYS[1])
	 *	else
	 *		return 0
	 * 	end
	 * 
	 * 上述Lua脚本告诉Redis: remove the key only if it exists and the value stored at the key is exactly the one I expect to be.
	 * 
	 * This is important in order to avoid removing a lock that was created by another client. 
	 * For example a client may acquire the lock, get blocked in some operation for longer than the lock validity time (the time at which the key will expire), 
	 * and later remove the lock, that was already acquired by some other client. 
	 * 
	 * Using just DEL is not safe as a client may remove the lock of another client. 
	 * With the above script instead every lock is “signed” with a random string, 
	 * so the lock will be removed only if it is still the one that was set by the client trying to remove it.
	 * 
	 * @param key 锁
	 * @param token 用于解锁的
	 * @return boolean 是否释放成功
	 * @on
	 */
	public static boolean unlock(String key, String token) {
		Jedis jedis = jedis();
		try {
			String setnxSha1 = shaHashs.computeIfAbsent("unlock.lua", x -> {
				logger.info("Load script {}", "unlock.lua");
				return jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/unlock.lua"));
			});

			String lockKey = nonBlockingLockKey(key);
			long result = (long) jedis.evalsha(setnxSha1, 1, lockKey, token);
			return result == 1L;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 在事务提交或者回滚后释放锁,即保证锁的释放
	 * 
	 * 使用这个方法需要配置Spring Bean
	 * <pre>{@code
	 * @Bean
	 * public TransactionEvents transactionEvents() {
	 *     return new TransactionEvents();
	 * }
	 * </pre>
	 * @param key
	 * @param token
	 */
	public static void unlockAnyway(String key, String token) {
		TransactionEvents.instance().afterCompletion(() -> unlock(key, token));
	}

	/**
	 * 返回一个分布式 List 实现 该list永不过期 注意如果List里面任意元素的属性改变了, 要重新Add进Set
	 * 
	 * @param listName
	 * @return CachedList
	 */
	/*
	 * public static <E> CachedList<E> list(String listName) { RList<E> list =
	 * redisson().getList(listName); return new RedissonArrayList<>(list); }
	 */

	/**
	 * 返回一个分布式 List 实现,并指定过期时间 注意如果List里面任意元素的属性改变了, 要重新Add进Set
	 * 
	 * @param listName
	 * @param timetoLive
	 * @param timeUnit
	 * @return CachedList
	 */
	/*
	 * public static <E> CachedList<E> list(String listName, long timetoLive,
	 * TimeUnit timeUnit) { RList<E> list = redisson().getList(listName);
	 * list.expire(timetoLive, timeUnit); return new RedissonArrayList<>(list); }
	 */

	/**
	 * 返回一个分布式Set 注意如果Set里面任意元素的属性改变了, 要重新Add进Set
	 * 
	 * @param name
	 * @return
	 */
	/*
	 * public static <E> CachedSet<E> cachedSet(String name) { return new
	 * RedissonSet<>(redisson().getSet(name)); }
	 */

	/**
	 * 预热JedisPool <p>
	 * 由于一些原因(例如超时时间设置较小原因), 有的项目在启动成功后会出现超时。
	 * JedisPool定义最大资源数、最小空闲资源数时, 不会真的把Jedis连接放到池子里, 
	 * 第一次使用时, 池子没有资源使用, 会new Jedis, 使用后放到池子里, 可能会有一定的时间开销, 
	 * 所以也可以考虑在JedisPool定义后, 为JedisPool提前进行预热, 例如以最小空闲数量为预热数量.
	 * @on
	 */
	public static void warmUp() {
		// 不卡住, 不影响Spring的启动
		Executors.newSingleThreadExecutor().execute(() -> {
			int maxIdle = defaultPool.getNumIdle();
			List<Jedis> minIdleJedisList = new ArrayList<Jedis>(maxIdle);

			for (int i = 0; i < maxIdle; i++) {
				Jedis jedis = null;
				try {
					jedis = jedis();
					minIdleJedisList.add(jedis);
					jedis.ping();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				} finally {
				}
			}

			for (int i = 0; i < maxIdle; i++) {
				Jedis jedis = null;
				try {
					jedis = minIdleJedisList.get(i);
					jedis.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			List<String> scripts = asList("rateLimit.lua", "setnx.lua");
			Jedis jedis = jedis();
			scripts.forEach((script) -> {
				shaHashs.computeIfAbsent(script, (x) -> {
					String sha1 = jedis.scriptLoad(IOUtils.readClassPathFile("/lua-scripts/" + script));
					logger.debug("Load lua script: {}, SHA1: ", "/lua-scripts/" + script, sha1);
					return sha1;
				});
			});
		});
	}

	private static byte[] toBytes(Object obj) {
		if (obj == null) {
			return new byte[0];
		}
		String primitive = PrimitiveUtils.toString(obj);// 先检查一下是不是原子类型, 是的话直接toString
		if (primitive != null) {
			return primitive.getBytes(UTF_8);
		} else if (String.class.isInstance(obj)) {
			return ((String) obj).getBytes(UTF_8);
		} else if (Collection.class.isInstance(obj)) {
			return JacksonUtils.toBytes(obj);
		} else {// key不可序列化
			return JacksonUtils.toBytes(obj);
		}
	}

	private static byte[] toBytes(String value) {
		if (value == null) {
			return new byte[0];
		}
		return value.getBytes(UTF_8);
	}

	private static byte[] toBytes(long value) {
		return String.valueOf(value).getBytes(UTF_8);
	}

	private static byte[] toBytes(int value) {
		return String.valueOf(value).getBytes(UTF_8);
	}

	@SuppressWarnings("unused")
	private static byte[] toBytes(double value) {
		return String.valueOf(value).getBytes(UTF_8);
	}

	@SuppressWarnings("unused")
	private static byte[] toBytes(BigDecimal value) {
		if (value == null) {
			return new byte[0];
		}
		return String.valueOf(value).getBytes(UTF_8);
	}

	private static byte[] toBytes(long time, TimeUnit timeUnit) {
		long seconds = timeUnit.toSeconds(time);
		return toBytes(seconds);
	}

	private static byte[][] toBytes(List<?> values) {
		List<byte[]> bytes = values.stream()
				.map((value) -> toBytes(value))
				.collect(Collectors.toList());
		byte[][] bytesArrays = new byte[values.size()][];
		for (int i = 0; i < bytesArrays.length; i++) {
			bytesArrays[i] = bytes.get(i);
		}

		return bytesArrays;
	}

	private static byte[][] toBytes(Object... values) {
		List<byte[]> bytes = asList(values).stream()
				.map((value) -> toBytes(value))
				.collect(Collectors.toList());
		byte[][] bytesArrays = new byte[values.length][];
		for (int i = 0; i < bytesArrays.length; i++) {
			bytesArrays[i] = bytes.get(i);
		}

		return bytesArrays;
	}

	@SuppressWarnings("unchecked")
	private static <T> T toObject(byte[] data, Class<T> clazz) {
		if (data == null || data.length == 0) {
			return null;
		}
		if (clazz.equals(String.class)) {
			return (T) new String(data, UTF_8);
		}
		T result = PrimitiveUtils.toPrimitive(data, clazz);
		if (result != null) {
			return result;
		}
		return JacksonUtils.toObject(toString(data), clazz);
	}

	private static <T> List<T> toList(byte[] value, Class<T> clazz) {
		if (value == null || value.length == 0) {
			return new ArrayList<>();
		}
		String json = toString(value);
		return JacksonUtils.toList(json, clazz);
	}

	private static String toString(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return new String(data, UTF_8);
	}

	public static Long toLong(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return new Long(new String(data, UTF_8));
	}

	private static int toSeconds(int time, TimeUnit timeUnit) {
		Long timeoutSeconds = timeUnit.toSeconds(time);
		return timeoutSeconds.intValue();
	}

	/**
	 * 给传入的非阻塞分布式锁的key加上前缀和后缀
	 * 
	 * @param originalKey
	 * @return
	 */
	private static String nonBlockingLockKey(String originalKey) {
		return NON_BLOCKING_LOCK_PREDIX + originalKey + NON_BLOCKING_LOCK_SUFFIX;
	}
}
