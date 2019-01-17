package com.loserico.cache.redis.factory;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.commons.resource.PropertyReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

public class JedisSentinelPoolFactory implements PoolFactory {

	private static final Logger logger = LoggerFactory.getLogger(JedisSentinelPoolFactory.class);

	@Override
	public Pool<Jedis> createPool(PropertyReader propertyReader) {
		//host:port,host:port,host:port
		String sentinels = propertyReader.getString("redis.sentinels");
		String password = propertyReader.getString("redis.password");
		int timeout = propertyReader.getInt("redis.timeout", 5000); //默认5秒超时
		int db = propertyReader.getInt("redis.db", 0);

		JedisSentinelPool sentinelPool = null;
		if (StringUtils.isNotBlank(password)) {
			sentinelPool = new JedisSentinelPool("loserSentinelPool",
					asList(sentinels.split(",")).stream().collect(toSet()),
					config(propertyReader),
					timeout,
					password,
					db);
		} else {
			sentinelPool = new JedisSentinelPool("loserSentinelPool",
					asList(sentinels.split(",")).stream().collect(toSet()),
					config(propertyReader),
					timeout,
					null,
					db);
		}

		logger.info("Current master: {}", sentinelPool.getCurrentHostMaster().toString());
		return sentinelPool;
	}

}
