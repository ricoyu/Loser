package com.loserico.cache.redis.factory;

import com.loserico.commons.resource.PropertyReader;

public final class JedisPoolFactories {

	private static final PropertyReader propertyReader = new PropertyReader("redis");

	public static PoolFactory poolFactory() {
		/*
		 * Redis的部署类型：single, sentinel, cluster
		 * redis.sentinels 属性存在则采用sentinel形式
		 * 否则 Redis 单节点
		 * @on
		 */
		String sentinels = propertyReader.getString("redis.sentinels");
		if (sentinels == null || "".equals(sentinels.trim())) {
			return new JedisPoolFactory();
		} else {
			return new JedisSentinelPoolFactory();
		}
	}
}
