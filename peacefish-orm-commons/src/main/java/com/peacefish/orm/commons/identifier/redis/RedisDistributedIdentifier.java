package com.peacefish.orm.commons.identifier.redis;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import com.peacefish.orm.commons.identifier.Identifiable;

/**
 * 分布式主键生成器
 * <p>
 * Copyright: Copyright (c) 2018-08-11 21:43
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedisDistributedIdentifier implements IdentifierGenerator, Configurable {

	private RedisDistributedIdentifierCache identifierCache;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		String namespace = params.getProperty("namespace", "distributed_identifier").toLowerCase();
		String schema = params.getProperty("schema", "redis_seq").toLowerCase();
		String table = params.getProperty("table", "default").toLowerCase();
		String size = params.getProperty("fetch_size", "10"); //本地缓存10个id

		String key = namespace + ":" + schema + ":" + table;
		Long fetchSize = Long.parseLong(size);
		//同一JVM中同一namespace下的同一path，只有一个RedisIdentifierCache实例
		this.identifierCache = RedisDistributedIdentifierCacheFactory.getIdentifierCache(key, fetchSize);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) {
		if (obj instanceof Identifiable) {
			Identifiable identifiable = (Identifiable) obj;
			Serializable id = identifiable.getId();
			if (id != null) {
				return id;
			}
		}

		long id = identifierCache.nextIdentifer();
		return id;
	}
}