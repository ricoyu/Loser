package com.loserico.commons.utils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 集合操作工具类
 * <p>
 * Copyright: Copyright (c) 2017-12-13 16:38
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class CollectionUtils {

	/**
	 * 判断集合不为null并且包含至少一个元素
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(Collection collection) {
		if(collection == null || collection.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 如果collection不为空则消费其每个元素<br/>
	 * 如果任意一个元素是null，那么该元素不会被消费
	 * @param collection
	 * @param consumer
	 * @on
	 */
	public static <E> void forEach(Collection<E> collection, Consumer<? super E> consumer) {
		if(isNotEmpty(collection)) {
			for (E object : collection) {
				if(object == null) {
					continue;
				}
				consumer.accept(object);
			}
		}
	}
}
