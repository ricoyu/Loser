package com.loserico.orm.hibernate.transformer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

public class ResultTransformerFactory {

	//命名sql查询的name+resultClass的名字作为键，相应的ValueHandlerResultTransformer作为value
	private static final ConcurrentHashMap<String, ValueHandlerResultTransformer> cache = new ConcurrentHashMap<>();

	private ResultTransformerFactory() {
	}

	public static ValueHandlerResultTransformer getResultTransformer(String queryName, Class<?> resultClass) {
		return getResultTransformer(queryName, resultClass, null);
	}

	public static ValueHandlerResultTransformer getResultTransformer(String queryName, Class<?> resultClass, String queryMode) {
		return getResultTransformer(queryName, resultClass, queryMode, null);
	}
	
	public static ValueHandlerResultTransformer getResultTransformer(String queryName, Class<?> resultClass, String queryMode,
			Set<String> enumLookupProperties) {
		Assert.notNull(resultClass);
		Assert.notNull(queryName);
		String key = queryName + resultClass.getName();
		return cache.computeIfAbsent(key, (k) -> {
			ValueHandlerResultTransformer transformer = new ValueHandlerResultTransformer(resultClass, queryMode);
			if(enumLookupProperties != null) {
				transformer.setEnumLookupProperties(enumLookupProperties);
			}
			return transformer;
		});
	}
}
