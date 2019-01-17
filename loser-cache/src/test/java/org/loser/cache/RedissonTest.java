package org.loser.cache;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * http://www.baeldung.com/redis-redisson
 * 
 * Redisson supports connections to the following Redis configurations:
 * 	- Single node
 *  - Master with slave nodes
 *  - Sentinel nodes
 *  - Clustered nodes
 *  - Replicated nodes
 * 
 * <p>
 * Copyright: Copyright (c) 2018-05-15 21:24
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedissonTest {

	@Test
	public void testSingleInstanceConnectLocalhost6379() {
		RedissonClient client = Redisson.create();
	}

	/**
	 * We specify Redisson configurations in an instance of a Config object and then
	 * pass it to the create method. Above, we specified to Redisson that we want to
	 * connect to a single node instance of Redis. To do this we used the Config
	 * object’s useSingleServer method. This returns a reference to a
	 * SingleServerConfig object.
	 */
	@Test
	public void testSingleNode() {
		Config config = new Config();
		/*
		 * redis:// 这个前缀(schema)是必须的，因为Redisson不仅支持Redis
		 * 
		 * 		 useSingleServer 			– for single node instance.
		 * useMasterSlaveServers 	– for master with slave nodes. 
		 * useSentinelServers – for sentinel nodes. 
		 * useClusterServers – for clustered nodes. 
		 * useReplicatedServers – for replicated nodes. 
		 */
		config.useSingleServer()
			.setAddress("redis://192.168.102.106:6379")
			.setConnectionMinimumIdleSize(10)
			.setConnectionPoolSize(400)
			.setPassword("deepdata$")
			.setRetryAttempts(3)
			.setTimeout(3000)
			.setConnectTimeout(4000)
			.setDatabase(1);
		long begin = System.currentTimeMillis();
		RedissonClient client = Redisson.create(config);
		long end = System.currentTimeMillis();
		System.out.println("建立连接花费: " + (end - begin) +" 毫秒");
		
		begin = System.currentTimeMillis();
		RAtomicLong atomicLong = client.getAtomicLong("dick");
		System.out.println(atomicLong.get());
		System.out.println(atomicLong.getAndIncrement());
		System.out.println(atomicLong.getAndIncrement());
		end = System.currentTimeMillis();
		System.out.println("获取RAtomicLong对象并getAndIncrement三次花费: " + (end - begin) + " 毫秒");
		
	}
}
