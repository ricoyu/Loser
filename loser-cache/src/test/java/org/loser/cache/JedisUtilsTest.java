package org.loser.cache;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.cache.interfaze.BlockingQueue;
import com.loserico.cache.redis.collection.ConcurrentMap;
import com.loserico.cache.redis.collection.ExpirableMap;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.Semaphore;
import com.loserico.cache.redis.concurrent.atomic.AtomicLong;
import com.loserico.commons.jackson.JacksonUtils;
import com.peacefish.spring.concurrent.ConcurrentTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * https://github.com/xetorthio/jedis/tree/ca4415f17aafd2d5016bee6b687cfffdf554544c/src/test/java/redis/clients/jedis/tests
 * 
 * <p>
 * Copyright: Copyright (c) 2018-05-12 17:47
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class JedisUtilsTest {

	@Test
	public void testSetGetStr() {
		System.out.println(JedisUtils.get("foo"));

		boolean result = JedisUtils.set("foo", "bar");
		System.out.println(result);
		System.out.println(JedisUtils.get("foo"));
		/*Jedis jedis = JedisUtils.getJedis(); System.out.println(jedis.get("foo"));
		jedis.close();
		
		System.out.println(JedisUtils.get("foo"));
		System.out.println(JedisUtils.get("foo", String.class));
		System.out.println(JedisUtils.get("foo"));*/

		/*
		 * System.out.println("==========="); boolean result2 =
		 * JedisUtils.set(BigDecimal.valueOf(321L), BigDecimal.valueOf(123));
		 * System.out.println("result2: " + result2);
		 * System.out.println(JedisUtils.get(BigDecimal.valueOf(321L),
		 * BigDecimal.class));
		 */

		/*
		 * System.out.println("==========="); boolean result3 = JedisUtils.set(123,
		 * LocalDate.now()); System.out.println("result3: " + result3); LocalDate now =
		 * JedisUtils.get(123, LocalDate.class); System.out.println(now);
		 */

		/*
		 * System.out.println("==========="); NonSerializable obj = new
		 * NonSerializable(BigDecimal.valueOf(100L)); String result4 =
		 * JedisUtils.set(obj, "hello"); System.out.println("result4: "+ result4);
		 * String value = JedisUtils.get(obj); System.out.println("value: " + value);
		 */

		/*
		 * System.out.println("==========="); NonSerializable obj = new
		 * NonSerializable(BigDecimal.valueOf(100L)); // boolean result5 =
		 * JedisUtils.set(obj, obj); // System.out.println("result5: " + result5);
		 * NonSerializable value = JedisUtils.get(obj, NonSerializable.class);
		 * System.out.println("value: " + value.getAmount());
		 * 
		 * System.out.println("==========="); NonSerializable obj2 = new
		 * NonSerializable(BigDecimal.valueOf(100L));
		 * System.out.println(JedisUtils.exists(obj2));
		 */
	}

	@Test
	public void testHsetHget() {
		JedisUtils.HASH.hset("studentNames", 123, "Justin Qiu Ohyuzhen");
		System.out.println(JedisUtils.HASH.hget("studentNames", 123));
	}

	@Test
	public void testKeys() {
		boolean success = JedisUtils.setnx("nonexists", "hi", 1, TimeUnit.SECONDS);
		System.out.println(success);
	}

	@Test
	public void testRateLimit() {
		JedisUtils.warmUp();
		for (int i = 0; i < 15; i++) {
			boolean exceed = JedisUtils.rateLimit("192.168.2.199", 4, 12);
			System.out.println(exceed);
		}
	}

	@Test
	public void testSetnx() {
		boolean set = JedisUtils.setnx("foos", "bar", 12, TimeUnit.SECONDS);
		System.out.println("设置成功：" + set);
	}

	@Test
	public void testSet() {
		System.out.println(JedisUtils.set("foo", "bar"));
	}

	@Test
	public void testPublish() {
		// JedisUtils.publish("foo2", "hello");
		// JedisUtils.publish("foo2", 123);
		// JedisUtils.publish("foo2", new NonSerializable(BigDecimal.valueOf(100L)));
		JedisUtils.publish("foo2", LocalDate.now());
		System.out.println("published");
	}

	@Test
	public void testSubscribe() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		JedisPubSub jedisPubSub = JedisUtils.subscribe("foo2",
				(channel, message) -> {
					System.out.println(channel + ": " + message);
					countDownLatch.countDown();
				},
				(channel, subscribedChannels) -> {
					System.out.println("取消订阅频道: " + channel + "频道数： " + subscribedChannels);
				});
		System.out.println("subscribed");
		ConcurrentTemplate.await(countDownLatch);

		System.out.println("unsubscribed");
		jedisPubSub.unsubscribe("foo2");

		System.out.println("terminated");
	}

	@Test
	public void testMap() {
		Map<String, String> map = new HashMap<>();
		map.put("rico", "俞雪华");
		map.put("vivi", "钱吴维");
		AtomicLongArray statistic = JedisUtils.HASH.hmsetGeneric("family", map);
		System.out.println("Updated: " + statistic.get(0));
		System.out.println("Inserted: " + statistic.get(1));
	}

	@Test
	public void testGetSetMap() {
		/*
		 * Map<Long, String> studentNames = new HashMap<>(); studentNames.put(123L,
		 * "Justin Oh Yuzhen"); studentNames.put(111L, "Kayla Tan");
		 * JedisUtils.hmsetGeneric("studentNames", studentNames);
		 * 
		 * Map<Long, String> cachedStudentNames = JedisUtils.hgetAll("studentNames",
		 * Long.class, String.class); System.out.println(toJson(cachedStudentNames));
		 * 
		 * JedisUtils.hset("studentNames", 111L, "Kayla Tan changed");
		 * cachedStudentNames = JedisUtils.hgetAll("studentNames", Long.class,
		 * String.class); System.out.println(toJson(cachedStudentNames));
		 */
		/*
		 * String studentName = JedisUtils.hget("studentNames", 123); Map<Long, String>
		 * modifiedStudentNames = JedisUtils.hmget("studentNames", asList(123L, 111L),
		 * String.class); System.out.println(toJson(modifiedStudentNames));
		 */

		/*
		 * System.out.println(JedisUtils.hexists("studentNames", 123L));
		 * System.out.println(JedisUtils.hexists("studentNames", 1223L));
		 */

		Map<Long, String> studentNames = new HashMap<>();
		studentNames.put(123L, "Justin Oh Yuzhen ZHENZHEN");
		studentNames.put(789L, "ZAIZAI");
		JedisUtils.HASH.hmsetGeneric("studentNames", studentNames);

		System.out.println(JedisUtils.HASH.hgetAll("studentNames"));
	}

	@Test
	public void testMapWithSetValue() {
		ConcurrentMap<Tickets, Set<Long>> referencedTicketsMap = RedissonUtils.concurrentMap("referencedTicketsMap");
		Set<Long> rebateIds = referencedTicketsMap.get(Tickets.REBATE);
		if (rebateIds == null) {
			rebateIds = new HashSet<>();
		}
		rebateIds.forEach(System.out::println);
		rebateIds.add(1234567L);
		// referencedTicketsMap.put(Tickets.REBATE, rebateIds);
	}

	@Test
	public void testMapWithHashKey() {
		String key = Tickets.PURCHASE_ORDER.name() + ":" + 39121684;
		// String hashKey = Hashing.sha256().hashString(key, UTF_8).toString();

		JedisUtils.set(key, 890316L);
	}

	@Test
	public void testGetHashKey() {
		String key = Tickets.PURCHASE_ORDER.name() + ":" + 39121684;
		// String hashKey = Hashing.sha256().hashString(key, UTF_8).toString();
		Long id = JedisUtils.get(key, Long.class);
		System.out.println(id);
	}

	@Test
	public void testExpirableMap() {
		ExpirableMap<String, Object> map = RedissonUtils.expirableMap("expirableMap");
		map.put("token", new User("xuehua", 36), 1, TimeUnit.MINUTES);
		map.put("username", "ricoyu520@gmail.com", 30, TimeUnit.SECONDS);
	}

	@Test
	public void testExpirableMapGet() throws InterruptedException {
		ExpirableMap<String, Object> map = RedissonUtils.expirableMap("expirableMap");
		String username = (String) map.get("username");
		System.out.println("第一次获取username[" + username + "]");
		User user = (User) map.get("token");
		System.out.println("User:\n" + JacksonUtils.toPrettyJson(user));

		SECONDS.sleep(25);
		System.out.println("过了25秒");
		username = (String) map.get("username");
		System.out.println("第二次获取username[" + username + "]");
		user = (User) map.get("token");
		System.out.println("第二次获取 User:\n" + JacksonUtils.toPrettyJson(user));

		SECONDS.sleep(5);
		System.out.println("过了30秒");
		username = (String) map.get("username");
		System.out.println("第三次获取username[" + username + "]");
		user = (User) map.get("token");
		System.out.println("第三次获取 User:\n" + JacksonUtils.toPrettyJson(user));

		SECONDS.sleep(90);
		System.out.println("又过了90秒");
		username = (String) map.get("username");
		System.out.println("第四次获取username[" + username + "]");
		user = (User) map.get("token");
		System.out.println("第四次获取 User:\n" + JacksonUtils.toPrettyJson(user));

		map.delete();
		System.out.println("map" + (map.isExists() ? " 没删掉" : " 删掉了"));
	}

	@Test
	public void testhgetAll() {
		Map<String, String> familyMap = JedisUtils.HASH.hgetAll("family");
		System.out.println(toJson(familyMap));
	}

	@Test
	public void testLock() throws InterruptedException {
		Lock lock = JedisUtils.lock("mylock", 12);
		System.out.println("第一次加锁：" + (lock.locked() ? "失败" : "成功"));

		Lock lock2 = JedisUtils.lock("mylock", 12);
		System.out.println("第二次加锁：" + (lock2.locked() ? "失败" : "成功"));

		boolean success = JedisUtils.unlock("mylock", "wrongToken");
		System.out.println("用错误的token解锁： " + (success ? "成功" : "失败"));

		// boolean success2 = JedisUtils.unlock("mylock", token);
		// System.out.println("用正确的token解锁： " + (success2 ? "成功" : "失败"));
		SECONDS.sleep(6);
		Lock lock3 = JedisUtils.lock("mylock", 12);
		System.out.println("6秒后再次加锁: " + (lock3.locked() ? "失败" : "成功"));

		SECONDS.sleep(6);
		Lock lock4 = JedisUtils.lock("mylock", 12);
		System.out.println("12秒后再次加锁: " + (lock4 == null ? "失败" : "成功"));
	}

	@Test
	public void testRedissonLock() throws InterruptedException {
		Lock lock = RedissonUtils.blockingLock("purchaseOrder", 1, TimeUnit.HOURS);
		if (lock.locked()) {
			System.out.println("第一次成功获取锁");
		}
		// SECONDS.sleep(4);
		// System.out.println("等待4秒后尝试获取锁");
		// long begin = System.currentTimeMillis();
		// JedisUtils.lock("purchaseOrder", 1, TimeUnit.MINUTES);
		// long end = System.currentTimeMillis();
		// System.out.println(((end - begin) / 1000) + " 秒后第二次成功获取锁");
		// System.out.println("第二次成功获取锁");
	}

	@Test
	public void testTryLock() {
		System.out.println("尝试获取锁");
		Lock lock = RedissonUtils.tryLock("purchaseOrder", 1, 10, SECONDS);
		System.out.println("等待1秒后" + ((lock.locked() ? "成功" : "失败") + " 获取锁"));
		lock.unlock();
	}

	@Test
	public void testIncr() {
		System.out.println(JedisUtils.incr("scp:purchase_order"));
		System.out.println(JedisUtils.incrBy("scp:purchase_order", 9));

	}

	@Test
	public void testBlockingQueue() {
		BlockingQueue<String> blockingQueue = RedissonUtils.blockingQueue("staffs", 10);
		System.out.println(blockingQueue.remainingCapacity());
	}

	@Test
	public void testSemaphore() throws InterruptedException {
		Semaphore semaphore = RedissonUtils.semaphore("semaphore1");
		semaphore.acquire();
		System.out.println("获取到信号量");
		semaphore.release();
		System.out.println("释放信号量");
	}

	@Test
	public void testAtomicLong() {
		AtomicLong atomicLong = RedissonUtils.atomicLong("/seq");
		long initValue = atomicLong.get();
		System.out.println("initValue: " + initValue);
		long incrValue = atomicLong.addAndGet(1L);
		System.out.println("incrValue: " + incrValue);
	}

	@Test
	public void testAtomicLongWithExpiretion() {
		AtomicLong atomicLong = RedissonUtils.atomicLong("/seq1");
		System.out.println(atomicLong.get());
		atomicLong.expire(3, TimeUnit.SECONDS);
		System.out.println(atomicLong.get());
		System.out.println(atomicLong.addAndGet(3));
	}

	@Test
	public void testToLong() {
		byte[] bytes = new String("").getBytes(UTF_8);
		Long value = JedisUtils.toLong(bytes);
		System.out.println(value);
	}

	public static class NonSerializable {
		private BigDecimal amount;

		public NonSerializable() {
		}

		public NonSerializable(BigDecimal amount) {
			super();
			this.amount = amount;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
	}

	public static class User implements Serializable {
		private static final long serialVersionUID = -490785789737320044L;

		public User() {
		}

		public User(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		private String name;

		private Integer age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

	}

	public enum Tickets {

		ALL(-1, "所有", "所有"),
		PURCHASE_ORDER(2, "采购单", "采购入库单"),
		PURCHASE_RETURNED(40, "采购退货单", "新退货"),
		SALES_RETURN(12, "售后退货", "售后退货"),
		SALES_RETURN_ITEM(1201, "售后退货单明细"),
		REBATE(22, "返点单", "返点单"),
		SETTLEMENT(23, "结算单", "结算单"),
		SETTLEMENT_ITEM(2301, "结算单项", "结算单项"),
		OTHER(26, "其他扣款", "其他扣款"),
		DSDJ(27, "代收代缴", "代收代缴"),
		GUARANTEE_MONEY(28, "质保金", "质保金"),
		YFKZYD(31, "预付款转移单", "预付款转移单"),
		PURCHASE_RETURNED_OLD(6, "采购退货单（旧）", "采购退货单(旧)"),
		TC_FEE(44, "TC运费", "TC运费"),
		PURCHASE_LOT(55, "采购批次单", "采购批次单"),
		SERVICE_CAR(60, "服务直通车", "服务直通车"),
		SXSJ(70, "实销实结销售单", "实销实结销售单"),
		SXSJ_THRKD(71, "实销实结退货入库单", "实销实结退货入库单"),
		GYKCGD(80, "公益款采购单", "公益款采购单"),
		TSTMFY(90, "图书贴码费用", "图书贴码费用"),
		ADV_FEE(100, "广告费", "广告费"),
		WQZYXSD(130, "物权转移销售单", "物权转移销售单"),
		WQZYTHD(131, "物权转移退货单", "物权转移退货单"),
		YB_FEE(140, "延保费用", "延保费用"),
		YB_FEE_RETURN(141, "延保费用退货", "延保费用退货");

		private int code;
		private String desc;
		private String alias;

		private Tickets(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		private Tickets(int code, String desc, String alias) {
			this.code = code;
			this.desc = desc;
			this.alias = alias;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		@Override
		public String toString() {
			return desc;
		}
	}

	@Test
	public void testGetList() {
		List<Long> centreIds = JedisUtils.getList("centreIds", Long.class);
		centreIds.forEach(System.out::println);
	}

	@Test
	public void testGetListLockProblem() {
		JedisUtils.getList("resources:4", String.class, () -> {
			return asList("a", "b", "v");
		}, 10, TimeUnit.MINUTES);
	}
}
