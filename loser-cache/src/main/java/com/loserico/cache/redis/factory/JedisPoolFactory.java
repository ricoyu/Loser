package com.loserico.cache.redis.factory;

import com.loserico.cache.redis.config.RedisProperties;
import com.loserico.cache.resource.PropertyReader;
import com.loserico.cache.utils.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

public class JedisPoolFactory implements PoolFactory {

	@Override
	public Pool<Jedis> createPool(PropertyReader propertyReader) {
		boolean defaultPoolEnabled = propertyReader.getBoolean("redis.default.enabled", true);
		if (!defaultPoolEnabled) {
			return null;
		}
		String host = propertyReader.getString("redis.host", "localhost");
		String overrideHost = System.getProperty("LOSER_REDIS_HOST");
		if (overrideHost != null && !overrideHost.isEmpty()) {
			host = overrideHost;
		} else {
			overrideHost = System.getenv("LOSER_REDIS_HOST");
			if (overrideHost != null && !overrideHost.isEmpty()) {
				host = overrideHost;
			}
		}
		int port = propertyReader.getInt("redis.port", 6379);
		String overridePort = System.getProperty("LOSER_REDIS_PORT");
		if (overridePort != null && !overridePort.isEmpty()) {
			port = Integer.parseInt(overridePort);
		} else {
			overridePort = System.getenv("LOSER_REDIS_PORT");
			if (overridePort != null && !overridePort.isEmpty()) {
				port = Integer.parseInt(overridePort);
			}
		}
		String password = propertyReader.getString("redis.password");
		String overridePassword = System.getProperty("LOSER_REDIS_PASSWORD");
		if (overridePassword != null && !overridePassword.isEmpty()) {
			password = overridePassword;
		}
		int timeout = propertyReader.getInt("redis.timeout", 5000); // 默认5秒超时
		int db = propertyReader.getInt("redis.db", 0);

		if (StringUtils.isNotBlank(password)) {
			return new JedisPool(config(propertyReader), host, port, timeout, password, db);
		} else {
			return new JedisPool(config(propertyReader), host, port, timeout, null, db);
		}
	}

	@Override
	public Pool<Jedis> createPool(RedisProperties redisProperties) {
		return new JedisPool(config(redisProperties),
				redisProperties.getHost(),
				redisProperties.getPort(),
				redisProperties.getTimeout(),
				redisProperties.getPassword(),
				redisProperties.getDatabase());
	}

}
