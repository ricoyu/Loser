package com.loserico.cache.redis.redisson.config;

import lombok.Data;

/**
 * 
 * <p>
 * Copyright: Copyright (c) 2019-06-03 14:22
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Data
public class RedissonProperties {

	/** Redis server address in host:port format. Use rediss:// protocol for SSL connection. */
	private String address = "redis://localhost:6379";

	private String host = "localhost";

	private int port = 6379;
	
	private String password;

	/** Database index used for Redis connection */
	private int database = 0;

	/** Minimum idle Redis subscription connection amount. */
	private int subscriptionConnectionMinimumIdleSize = 1;

	/** Redis subscription connection maximum pool size */
	private int subscriptionConnectionPoolSize = 50;

	/** Minimum idle Redis connection amount. */
	private int connectionMinimumIdleSize = 8;

	/** Redis connection maximum pool size. */
	private int connectionPoolSize = 32;

	/**
	 * DNS change monitoring interval. Applications must ensure the JVM DNS cache TTL is low enough
	 * to support this. Set -1 to disable. Multiple IP bindings for single hostname supported in
	 * Proxy mode.
	 */
	private int dnsMonitoringInterval = 5000;

	/**
	 * If pooled connection not used for a timeout time and current connections amount bigger than
	 * minimum idle connections pool size, then it will closed and removed from pool. Value in
	 * milliseconds.
	 */
	private int idleConnectionTimeout = 10000;

	/** Timeout during connecting to any Redis server. */
	private int connectTimeout = 5000;

	/**
	 * Redis server response timeout. Starts to countdown when Redis command was succesfully sent.
	 * Value in milliseconds.
	 */
	private int timeout = 3000;

	/**
	 * Error will be thrown if Redis command can't be sended to Redis server after retryAttempts.
	 * But if it sent succesfully then timeout will be started.
	 */
	private int retryAttempts = 3;
	/**
	 * Time interval after which another one attempt to send Redis command will be executed. Value
	 * in milliseconds.
	 */
	private int retryInterval = 1500;

	/** Subscriptions per Redis connection limit */

	private int subscriptionsPerConnection = 5;
	/** Defines PING command sending interval per connection to Redis. Set 0 to disable. */

	private int pingConnectionInterval = 0;

	/** Enables TCP keepAlive for connection. */
	private boolean keepAlive = false;

	/** Enables TCP noDelay for connection */
	private boolean tcpNoDelay = false;
}
