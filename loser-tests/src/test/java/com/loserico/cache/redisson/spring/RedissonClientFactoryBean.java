package com.loserico.cache.redisson.spring;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.FactoryBean;

public class RedissonClientFactoryBean implements FactoryBean<RedissonClient> {

	private String jsonConfig;

	@Override
	public RedissonClient getObject() throws Exception {
		Config config = Config.fromJSON(getClass().getClassLoader().getResourceAsStream(getJsonConfig()));
		return Redisson.create(config);
	}

	@Override
	public Class<RedissonClient> getObjectType() {
		return RedissonClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getJsonConfig() {
		return jsonConfig;
	}

	public void setJsonConfig(String jsonConfig) {
		this.jsonConfig = jsonConfig;
	}

}
