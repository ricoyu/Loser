package com.loserico.cache.redis.config;

public class RedisProperties {

	private String host;

	private int port;

	private int database;

	private String password;
	
	/** 建立连接的超时时间, 如果超过这个时间还连不上Redis就抛异常 */
	private int timeout = 5000;

	/** 资源池中最大连接数 */
	private int maxTotal = 8;

	/** 资源池允许最大空闲的连接数 */
	private int maxIdle = 8;
	
	private int minIdle = 8;

	/** 向资源池借用连接时是否做连接有效性检测(ping), 无效连接会被移除 */
	private boolean testOnBorrow = false;

	/** 向资源池归还连接时是否做连接有效性检测(ping), 无效连接会被移除 */
	private boolean testOnReturn = false;

	/** 是否开启jmx监控，可用于监控 */
	private boolean jmxEnabled = true;

	/** 当资源池用尽后，调用者是否要等待。有当为true时，下面的maxWaitMillis才会生效 */
	private boolean blockWhenExhausted = true;

	/** 当资源池连接用尽后，调用者的最大等待时间(单位为毫秒) */
	private int maxWaitMillis = -1;

	/** 是否开启空闲资源监测 */
	private boolean testWhileIdle = false;

	/** 空闲资源的检测周期(单位为毫秒) */
	private int timeBetweenEvictionRunsMillis = -1;

	/**
	 * 资源池中资源最小空闲时间(单位为毫秒), 达到此值后空闲资源将被移除
	 */
	private int minEvictableIdleTimeMillis = 1800000;
	
	/** 做空闲资源检测时，每次的采样数 */
	private int numTestsPerEvictionRun = 3;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isJmxEnabled() {
		return jmxEnabled;
	}

	public void setJmxEnabled(boolean jmxEnabled) {
		this.jmxEnabled = jmxEnabled;
	}

	public boolean isBlockWhenExhausted() {
		return blockWhenExhausted;
	}

	public void setBlockWhenExhausted(boolean blockWhenExhausted) {
		this.blockWhenExhausted = blockWhenExhausted;
	}

	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
