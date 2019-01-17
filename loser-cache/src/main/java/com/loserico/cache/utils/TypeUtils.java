package com.loserico.cache.utils;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.loserico.commons.jackson.JacksonUtils;

/**
 * Jackson 反序列化时复杂对象支持
 * <p>
 * Copyright: Copyright (c) 2018-09-13 14:28
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class TypeUtils {

	private static TypeFactory typeFactory = JacksonUtils.objectMapper().getTypeFactory();

	/**
	 * 反序列化成List<T>
	 * @param clazz
	 * @return JavaType
	 */
	public static <T> JavaType listType(Class<T> clazz) {
		return typeFactory.constructCollectionType(List.class, clazz);
	}

	/**
	 * 反序列化成Map<K, V>
	 * @param keyType
	 * @param valueType
	 * @return JavaType
	 */
	public static <K, V> JavaType mapType(Class<K> keyType, Class<V> valueType) {
		return typeFactory.constructMapType(HashMap.class, keyType, valueType);
	}

	/**
	 * 反序列化成 Map<K, 任意类型>
	 * @param keyType
	 * @param valueType
	 * @return JavaType
	 */
	public static <K, V> JavaType mapType(Class<K> keyType, JavaType valueType) {
		return typeFactory.constructMapType(HashMap.class, typeFactory.constructType(keyType), valueType);
	}

	/**
	 * 反序列化成Map<任意类型, 任意类型>
	 * @param keyType
	 * @param valueType
	 * @return JavaType
	 */
	public static <K, V> JavaType mapType(JavaType keyType, JavaType valueType) {
		return typeFactory.constructMapType(HashMap.class, keyType, valueType);
	}
}
