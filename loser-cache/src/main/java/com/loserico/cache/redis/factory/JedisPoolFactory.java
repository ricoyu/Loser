package com.loserico.cache.redis.factory;

import org.apache.commons.lang3.StringUtils;

import com.loserico.commons.resource.PropertyReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

public class JedisPoolFactory implements PoolFactory {
	
	@Override
	public Pool<Jedis> createPool(PropertyReader propertyReader) {
		String host = propertyReader.getString("redis.host");
		int port = propertyReader.getInt("redis.port", 6379);
		String password = propertyReader.getString("redis.password");
		int timeout = propertyReader.getInt("redis.timeout", 5000); //默认5秒超时
		int db = propertyReader.getInt("redis.db", 0);

		if (StringUtils.isNotBlank(password)) {
			return new JedisPool(config(propertyReader), host, port, timeout, password, db);
		} else {
			return new JedisPool(config(propertyReader), host, port, timeout, null, db);
		}
	}

}
