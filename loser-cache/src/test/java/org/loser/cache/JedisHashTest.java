package org.loser.cache;

import static com.loserico.cache.utils.KeyUtils.joinKey;
import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.cache.utils.TypeUtils;

public class JedisHashTest {

	@Test
	public void testHashValueAsList() {
		List<User> users = asList(new User("俞雪华", 30), new User("俞希哲", 4));
		JedisUtils.HASH.hset(joinKey("user", 1), "mine", users);

		Map<String, List<User>> userMap = JedisUtils.HASH.hgetAll(joinKey("user", 1), String.class, TypeUtils.listType(User.class));
		Map<String, List<User>> userMap2 = JedisUtils.HASH.hgetAll(joinKey("user", 1), TypeUtils.listType(User.class));
		System.out.println(toJson(userMap));
		System.out.println(toJson(userMap2));
	}

	@Test
	public void testHashGetSingleFieldAsMap() {
		List<User> users = asList(new User("俞雪华", 30), new User("俞希哲", 4));
		Map<Integer, List<User>> userMap = new HashMap<>();
		userMap.put(1, users);

		JedisUtils.HASH.hset("users", "xuehua", userMap);
		Map<Integer, List<User>> userMap2 = JedisUtils.HASH.hget("users", "xuehua",
				TypeUtils.mapType(Integer.class, TypeUtils.listType(User.class)));
		System.out.println(toJson(userMap2));
	}

	public static class User {
		private String name;

		private Integer age;

		public User() {
		}

		public User(String name, Integer age) {
			super();
			this.name = name;
			this.age = age;
		}

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
}
