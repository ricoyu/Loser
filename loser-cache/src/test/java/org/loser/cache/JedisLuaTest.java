package org.loser.cache;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.loserico.cache.auth.LoginResult;
import com.loserico.cache.redis.JedisUtils;
import com.loserico.cache.utils.IOUtils;
import com.loserico.commons.utils.DateUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisLuaTest {

	private static final Logger logger = LoggerFactory.getLogger(JedisLuaTest.class);

	private static JedisPool pool = null;

	@BeforeClass
	public static void setup() {
		pool = new JedisPool(new JedisPoolConfig(), "192.168.102.106", 6379, 100, "deepdata$");
	}

	@AfterClass
	public static void tearDown() {
		pool.close();
	}

	@Test
	public void testSingleNode() {
		Jedis jedis = new Jedis("192.168.102.106", 6379);
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		System.out.println(value);
	}

	@Test
	public void testHelloLua() {
		Jedis jedis = pool.getResource();
		Object object = jedis.eval(IOUtils.readClassPathFile("hello.lua"));
		System.out.println(object);
	}

	@Test
	public void testMapLua() {
		Jedis jedis = pool.getResource();
		Object object = jedis.eval(IOUtils.readClassPathFile("map-test.lua"));
		System.out.println(object);

		String result = JedisUtils.eval(IOUtils.readClassPathFile("map-test.lua"));
		System.out.println(result);
	}

	@Test
	public void testTime() {
		String script = IOUtils.readClassPathFile("time-test.lua");
		Object object = JedisUtils.eval(script);
		System.out.println(object);
	}
	
	@Test
	public void testZscore() {
		Jedis jedis = pool.getResource();
		Object object = jedis.eval(IOUtils.readClassPathFile("zset-test.lua"));
		System.out.println(object);
	}

	/**
	 * 获取脚本的SHA值
	 */
	@Test
	public void testScriptLoad() {
		Jedis jedis = pool.getResource();
		String script = IOUtils.readClassPathFile("hello.lua");
		String sha = jedis.scriptLoad(script);
		System.out.println(sha);

		System.out.println(Hashing.sha1().hashString(script, UTF_8).toString());
		;
	}

	/**
	 * EVAL 脚本的SHA值
	 */
	@Test
	public void testEvalSHA() {
		Jedis jedis = pool.getResource();
		Object object = jedis.evalsha("5ebc78cb94f2c69471c589d06352885df6c66091");
		System.out.println(object);

	}

	/**
	 * Accessing Keys and Arguments
	 * 
	 * Suppose we’re building a URL-shortener. 
	 * Each time a URL comes in we want to store it and return a unique number that can be used to access the URL later.
	 * 
	 * 调用形式为(逗号前面为KEYS， 后面是ARGV,参数个数不传也可以)：
	 *  redis-cli --eval incrset.lua links:counter links:urls , http://malcolmgladwellbookgenerator.com/
	 * We’ll use a Lua script to get a unique ID from Redis using INCR and immediately store the URL in a hash that is keyed by the unique ID:
	 * 	local link_id = redis.call("INCR", KEYS[1])
	 * 	redis.call("HSET", KEYS[2], link_id, ARGV[1])
	 * 	return link_id
	 * 
	 * KEYS 存KEY
	 * ARGV 存参数值
	 * 这两个数据结构都相当于数组，但是index从1开始。KEYS ARGV 要大写
	 * Table不能存nil值，如果往Table中塞了nil值，后续的值将被截断 [ 1, nil, 3, 4 ] 将截断成 [ 1 ]
	 * 
	 * We’re accessing two Lua tables, KEYS and ARGV. 
	 * Tables are associative arrays, and Lua’s only mechanism for structuring data. 
	 * For our purposes you can think of them as the equivalent of an array in whatever language you’re most comfortable with, 
	 * but note these two Lua-isms that trip up folks new to the language:
	 * 
	 * Tables cannot hold nil values. 
	 * If an operation would yield a table of [ 1, nil, 3, 4 ], the result will instead be [ 1 ] — the table is truncated at the first nil value.
	 * 
	 * When we invoke this script, we need to also pass along the values for the KEYS and ARGV tables. In the raw Redis protocol, the command looks like this:
	 * 	EVAL $incrset.lua 2 links:counter links:url, http://malcolmgladwellbookgenerator.com/
	 * 
	 * When calling EVAL, after the script we provide 2 as the number of KEYS that will be accessed, then we list our KEYS, and finally we provide values for ARGV.
	 * 
	 * Normally when we build apps with Redis Lua scripts, the Redis client library will take care of specifying the number of keys. 
	 * The above code block is shown for completeness, but here’s the easier way to do this on at the command line
	 * 
	 * 调用(客户端会帮我们处理参数个数，所以不传参数个数也是可以的，逗号前面的是KEYS， 后面的是ARGV	)：
	 * 	redis-cli --eval incrset.lua links:counter links:urls , http://malcolmgladwellbookgenerator.com/
	 * 
	 * When using --eval as above, the comma separates KEYS[] from ARGV[] items.
	 * 上面那段lua脚本按照传的参数展开后就相当于这样
	 * 	local link_id = redis.call("INCR", "links:counter")
	 * 	redis.call("HSET", "links:urls", link_id, "http://malcolmgladwellbookgenerator.com")
	 * 	return link_id
	 * 
	 * We’re accessing Redis for the first time here, using the call() function. 
	 * call()’s arguments are the commands to send to Redis: first we INCR <key>, then we HSET <key> <field> <value>. 
	 * These two commands will run sequentially — Redis won’t do anything else while this script executes, and it will run extremely quickly.
	 * 
	 * When writing Lua scripts for Redis, every key that is accessed should be accessed only by the KEYS table. 
	 * The ARGV table is used for parameter-passing — here it’s the value of the URL we want to store.
	 * @on
	 */
	@Test
	public void testLuaKeyArgs() {
		Jedis jedis = pool.getResource();
		String script = IOUtils.readClassPathFile("key-arg.lua");
		/*
		 * local link_id = redis.call("INCR", KEYS[1])
		 * redis.call("HSET", KEYS[2], link_id, ARGV[1])
		 * return link_id
		 * @on
		 */
		Object result = jedis.eval(script, 2, "links:cursor", "links:url", "http://pims.mulberrylearning.cn");
		System.out.println(result);
		jedis.close();
	}

	@Test
	public void testIncrIfExists() {
		Jedis jedis = pool.getResource();
		String script = IOUtils.readClassPathFile("incrIfExists.lua");
		String sha = jedis.scriptLoad(script);
		String sha2 = Hashing.sha1().hashString(script, UTF_8).toString();
		assertEquals(sha, sha2);
		Object result = jedis.evalsha(sha2, 1, "hitCount");
		System.out.println(result);
	}

	@Test
	public void testFunction() {
		String token = JedisUtils.eval(IOUtils.readClassPathFile("function-test.lua"),
				0,
				"token1");
		System.out.println(token);
	}

	@Test
	public void testMilis() {
		System.out.println(DateUtils.toEpochMilis(LocalDateTime.now()));
		System.out.println(SECONDS.toMillis(1) + "");
	}

	@Test
	public void testLogin() {
		String username = "ricoyu";
		String token = "token123123";
		long expires = 6L;
		UserDetails userDetails = new UserDetails("ricoyu", "俞雪华");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new GrantedAuthority("ROLE_ADMIN"));
		authorities.add(new GrantedAuthority("user:del"));
		LoginInfo loginInfo = new LoginInfo("device:123123asdhjkahsdad5");
		LoginResult<LoginInfo> loginResult = JedisUtils.AUTH.login(username, 
				token, 
				expires, TimeUnit.SECONDS, 
				userDetails, 
				authorities,
				loginInfo);
		System.out.println(toJson(loginResult));
	}
	
	@Test
	public void testAuth() {
		String username = JedisUtils.AUTH.auth("token123123");
		System.out.println(username);
	}

	@Test
	public void testClearExpired() {
		Object result = JedisUtils.AUTH.clearExpired(); 
		System.out.println(result);
		 
	}

	@Test
	public void testLogout() {
		Object result = JedisUtils.eval(IOUtils.readClassPathFile("spring-security-auth.lua"),
				0,
				"logout",
				"token123123");
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		/*JedisUtils.subscribe(JedisUtils.AUTH.AUTH_TOKEN_EXPIRE_CHANNEL, (channel, message) -> {
			System.out.println("Channel: " + channel + " message: " + message);
		});*/
		JedisUtils.AUTH.onTokenExpire((expiredMap) -> {
			for (String token : expiredMap.keySet()) {
				System.out.println("Token: " + token +" 已过期");
				System.out.println("额外的登录信息: " + toJson(expiredMap.get(token)));
			}
		});
	}
	public static class UserDetails {
		private String username;

		private String fullname;

		public UserDetails() {
		}

		public UserDetails(String username, String fullname) {
			this.username = username;
			this.fullname = fullname;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getFullname() {
			return fullname;
		}

		public void setFullname(String fullname) {
			this.fullname = fullname;
		}

	}

	public static class GrantedAuthority {

		public GrantedAuthority() {
		}

		public GrantedAuthority(String authority) {
			this.authority = authority;
		}

		private String authority;

		public String getAuthority() {
			return authority;
		}

		public void setAuthority(String authority) {
			this.authority = authority;
		}
	}

	public static class LoginInfo {
		public LoginInfo() {
		}

		public LoginInfo(String deviceId) {
			this.deviceId = deviceId;
		}

		private String deviceId;

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
	}

}
