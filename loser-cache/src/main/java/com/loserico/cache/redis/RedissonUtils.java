package com.loserico.cache.redis;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RLock;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.cache.exception.OperationNotSupportedException;
import com.loserico.cache.redis.cache.interfaze.BlockingDeque;
import com.loserico.cache.redis.cache.interfaze.BlockingQueue;
import com.loserico.cache.redis.collection.ConcurrentMap;
import com.loserico.cache.redis.collection.ExpirableMap;
import com.loserico.cache.redis.concurrent.ExpirableSemaphore;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.Semaphore;
import com.loserico.cache.redis.concurrent.atomic.AtomicLong;
import com.loserico.cache.redis.redisson.atomic.RedissonAtomicLong;
import com.loserico.cache.redis.redisson.collection.RedissonBlockingDeque;
import com.loserico.cache.redis.redisson.collection.RedissonBlockingQueue;
import com.loserico.cache.redis.redisson.collection.RedissonConcurrentMap;
import com.loserico.cache.redis.redisson.collection.RedissonExpirableMap;
import com.loserico.cache.redis.redisson.concurrent.RedissonLock;
import com.loserico.cache.redis.redisson.concurrent.RedissonSemaphore;
import com.loserico.cache.redis.redisson.config.RedissonFactory;
import com.loserico.cache.resource.PropertyReader;

public final class RedissonUtils {
	private static final Logger logger = LoggerFactory.getLogger(RedissonUtils.class);

	private static RedissonClient client = null;
	private static final PropertyReader propertyReader = new PropertyReader("redis");
	private static boolean redissonEnabled = propertyReader.getBoolean("redisson.enable", false);
	private static boolean redissonLazyInit = propertyReader.getBoolean("redisson.lazy", true);
	
	private static volatile Supplier<RedissonClient> redissonClientSupplier = null;

	static {
		if (client == null) {
			synchronized (RedissonUtils.class) {
				if (client == null && redissonEnabled && !redissonLazyInit) {
					client = RedissonFactory.createRedissonClient(propertyReader);
				}
			}
		}
	}

	private static RedissonClient redisson() {
		if (!redissonEnabled) {
			throw new OperationNotSupportedException("Redisson not enabled, try set redisson.enable=true");
		}
		if (client == null && redissonLazyInit) {
			synchronized (RedissonUtils.class) {
				if (client == null) {
					client = RedissonFactory.createRedissonClient(propertyReader);
				}
			}
		}
		return client;
	}

	/**
	 * <pre>
	 * <b>非公平锁</b></p>
	 * 非公平锁
	 * 尝试获取锁，如果锁已经被其他客户端获取，本线程将一直阻塞。
	 * 成功获取锁后，如果不手工释放锁，leaseTime 个单位时间后锁自动释放。
	 * 
	 * 锁在Redis里面是Hash结构
	 * 同一个线程里面可以重复多次获取同一把锁，但是不同线程之间同时只有一个线程可以获取同一把锁</pre>
	 * @param name
	 * @param leaseTime the maximum time to hold the lock after granting it, before
	 *            automatically releasing it if it hasn't already been released by
	 *            invoking unlock. If leaseTime is -1, hold the lock until explicitly
	 *            unlocked.
	 * @param unit
	 * @return Lock
	 * @on
	 */
	public static Lock blockingLock(String name, long leaseTime, TimeUnit unit) {
		RLock lock = redisson().getLock(name);
		lock.lock(leaseTime, unit);
		return new RedissonLock(lock, true);
	}

	/**
	 * <pre>
	 * <b>公平锁</b></p>
	 * 公平锁
	 * 尝试获取锁，如果锁已经被其他客户端获取，本线程将一直阻塞。
	 * 成功获取锁后，如果不手工释放锁，leaseTime 个单位时间后锁自动释放。
	 * 
	 * 锁在Redis里面是Hash结构
	 * 同一个线程里面可以重复多次获取同一把锁，但是不同线程之间同时只有一个线程可以获取同一把锁</pre>
	 * @param name
	 * @param leaseTime the maximum time to hold the lock after granting it, before
	 *            automatically releasing it if it hasn't already been released by
	 *            invoking unlock. If leaseTime is -1, hold the lock until explicitly
	 *            unlocked.
	 * @param unit
	 * @return Lock
	 * @on
	 */
	public static Lock fairLock(String name, long leaseTime, TimeUnit unit) {
		RLock lock = redisson().getFairLock(name);
		lock.lock(leaseTime, unit);
		return new RedissonLock(lock, true);
	}

	/**
	 * <pre>
	 * <b>非公平锁</b></p>
	 * 尝试获取锁，如果锁已经被其他客户端获取，本线程将阻塞。
	 * 最多等待 waitTime 个单位时间
	 * 成功获取锁后，如果不手工释放锁，leaseTime 个单位时间后锁自动释放。
	 * 
	 * 锁在Redis里面是Hash结构
	 * 同一个线程里面可以重复多次获取同一把锁，但是不同线程之间同时只有一个线程可以获取同一把锁</pre>
	 * @param name
	 * @param leaseTime the maximum time to hold the lock after granting it, before
	 *            automatically releasing it if it hasn't already been released by
	 *            invoking unlock. If leaseTime is -1, hold the lock until explicitly
	 *            unlocked.
	 * @param unit
	 * @return Lock
	 * @on
	 */
	public static Lock tryLock(String name, long waitTime, long leaseTime, TimeUnit unit) {
		RLock lock = redisson().getLock(name);
		try {
			boolean success = lock.tryLock(waitTime, leaseTime, unit);
			logger.info((success ? "Successfully" : "Failed") + " get lock of name");
			return new RedissonLock(lock, success);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
		return new RedissonLock(lock, false);
	}

	/**
	 * <pre>
	 * <b>公平锁</b></p>
	 * 尝试获取锁，如果锁已经被其他客户端获取，本线程将阻塞。
	 * 最多等待 waitTime 个单位时间
	 * 成功获取锁后，如果不手工释放锁，leaseTime 个单位时间后锁自动释放。
	 * 
	 * 锁在Redis里面是Hash结构
	 * 同一个线程里面可以重复多次获取同一把锁，但是不同线程之间同时只有一个线程可以获取同一把锁</pre>
	 * @param name
	 * @param leaseTime the maximum time to hold the lock after granting it, before
	 *            automatically releasing it if it hasn't already been released by
	 *            invoking unlock. If leaseTime is -1, hold the lock until explicitly
	 *            unlocked.
	 * @param unit
	 * @return Lock
	 * @on
	 */
	public static Lock tryFairLock(String name, long waitTime, long leaseTime, TimeUnit unit) {
		RLock lock = redisson().getFairLock(name);
		try {
			boolean success = lock.tryLock(waitTime, leaseTime, unit);
			logger.info((success ? "Successfully" : "Failed") + " get lock of name");
			return new RedissonLock(lock, success);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
		return new RedissonLock(lock, false);
	}

	/**
	 * 获取一个分布式信号量,这个信号量有 tickets 张票。
	 * leaseTime 个单位时间后自动释放船票
	 * 
	 * 注意Semaphore本身并没有设置过期时间
	 * 
	 * @param name
	 * @param leaseTime
	 * @param unit
	 * @return ExpirableSemaphore
	 * @on
	 */
	public static ExpirableSemaphore semaphore(String name, int tickets, long leaseTime, TimeUnit unit) {
		RPermitExpirableSemaphore semaphore = redisson().getPermitExpirableSemaphore(name);
		boolean permitsSetSuccess = semaphore.trySetPermits(tickets);
		logger.info("Set permit {} of semaphore {} {}", tickets, name, permitsSetSuccess);

		return new ExpirableSemaphore(semaphore, leaseTime, unit, permitsSetSuccess);
	}

	/**
	 * 获取一个分布式信号量,这个信号量只有一张票。leaseTime 个单位时间后自动释放船票
	 * 
	 * @param name
	 * @param leaseTime
	 * @param unit
	 * @return ExpirableSemaphore
	 */
	public static ExpirableSemaphore semaphore(String name, long leaseTime, TimeUnit unit) {
		RPermitExpirableSemaphore semaphore = redisson().getPermitExpirableSemaphore(name);
		boolean permitsSetSuccess = semaphore.trySetPermits(1);
		logger.info("Set permit {} of semaphore {} {}", 1, name, permitsSetSuccess);
		return new ExpirableSemaphore(semaphore, leaseTime, unit, permitsSetSuccess);
	}

	/**
	 * 根据指定名称创建一个分布式的Long
	 * 
	 * @param name
	 * @return AtomicLong
	 */
	public static AtomicLong atomicLong(String name) {
		return new RedissonAtomicLong(redisson().getAtomicLong(name));
	}

	/**
	 * 根据指定名称创建一个分布式的ConcurrentMap<K, V> 注意value对应的对象属性改变了，需要再put(key, value)回去
	 * 
	 * @param name
	 * @return ConcurrentMap
	 */
	public static <K, V> ConcurrentMap<K, V> concurrentMap(String name) {
		return new RedissonConcurrentMap<>(redisson().getLocalCachedMap(name, LocalCachedMapOptions.defaults()));
	}

	/**
	 * 根据指定名称创建一个分布式的ExpirableMap<K, V> 可以为Map的每个key指定过期时间
	 * 
	 * @param name
	 * @return ConcurrentMap
	 */
	public static <K, V> ExpirableMap<K, V> expirableMap(String name) {
		return new RedissonExpirableMap<>(redisson().getMapCache(name));
	}

	/**
	 * 分布式的阻塞队列，队列长度没有限制
	 * 
	 * @param name
	 * @param capacity
	 * @return BlockingQueue
	 */
	public static <E> BlockingQueue<E> blockingQueue(String name) {
		RBoundedBlockingQueue<E> blockingQueue = redisson().getBoundedBlockingQueue(name);
		return new RedissonBlockingQueue<>(blockingQueue);
	}

	/**
	 * 分布式的阻塞队列，有容量限制
	 * 
	 * @param name
	 * @param capacity
	 * @return BlockingQueue
	 */
	public static <E> BlockingQueue<E> blockingQueue(String name, int capacity) {
		RBoundedBlockingQueue<E> blockingQueue = redisson().getBoundedBlockingQueue(name);
		boolean success = blockingQueue.trySetCapacity(capacity);
		return new RedissonBlockingQueue<>(blockingQueue, success);
	}

	/**
	 * 分布式的双端队列
	 * 
	 * @param name
	 * @param capacity
	 * @return BlockingQueue
	 */
	public static <E> BlockingDeque<E> blockingDeque(String name) {
		RBlockingDeque<E> blockingDeque = redisson().getBlockingDeque(name);
		return new RedissonBlockingDeque<>(blockingDeque);
	}

	/**
	 * 获取一个分布式信号量,这个信号量只有一张票。
	 * 
	 * @param name
	 * @return Semaphore
	 */
	public static Semaphore semaphore(String name) {
		RSemaphore semaphore = redisson().getSemaphore(name);
		boolean permitsSetSuccess = semaphore.trySetPermits(1);
		logger.info("Set permit {} of semaphore {} {}", 1, name, permitsSetSuccess);
		return new RedissonSemaphore(semaphore, permitsSetSuccess);
	}

	/**
	 * 获取一个分布式信号量,这个信号量有 tickets 张票。
	 * 
	 * 用法是不同客户端传相同的name以获取同一个Semaphore。只有第一个客户端可以设置船票数，后续设置的船票数都失败，但只要还有船票，后续客户端还是可以acquire成功
	 * @param name
	 * @return Semaphore
	 * @on
	 */
	public static Semaphore semaphore(String name, int tickets) {
		RSemaphore semaphore = redisson().getSemaphore(name);
		/*
		 * trySetPermits的逻辑写在这个lua脚本里面了
		 * local value = redis.call('get', KEYS[1]);
		 * if (value == false or value == 0) then 
		 *     redis.call('set', KEYS[1], ARGV[1]);
		 *     redis.call('publish', KEYS[2], ARGV[1]);
		 *     return 1;
		 * end;
		 * return 0;
		 * @on
		 */
		boolean success = semaphore.trySetPermits(tickets);
		logger.info("Set permit {} of semaphore {} {}", tickets, name, success);
		return new RedissonSemaphore(semaphore, success);
	}

}
