package com.loserico.commons.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 哈希相关函数即算法实现
 * <p>
 * Copyright: Copyright (c) 2019-08-15 08:34
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class HashUtils {

	/**
	 * 获取对象的哈希值。采用JDK8 中HashMap使用的hash算法
	 * <p/>
	 * Hash, 一般翻译做"散列", 也有直接音译为"哈希"的, 就是把任意长度的输入, 通过散列算法, 变换成固定长度的输出, 该输出就是散列值
	 * 
	 * @param key
	 * @return int
	 */
	public static int hash8(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}

	/**
	 * 获取对象的哈希值。采用JDK7 中HashMap使用的hash算法
	 * <p/>
	 * Hash, 一般翻译做"散列", 也有直接音译为"哈希"的, 就是把任意长度的输入, 通过散列算法, 变换成固定长度的输出, 该输出就是散列值
	 * 
	 * @param key
	 * @return int
	 */
	public static int hash7(Object key) {
		int h = ThreadLocalRandom.current().nextInt(3000);

		h ^= key.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * 根据哈希值以及数组或者队列的长度, 计算应该落在哪里
	 * @param hash
	 * @param length
	 * @return index下标
	 */
	public static int indexFor(int hash, int length) {
		if (length % 2 == 0) {//leng是偶数的情况
			return hash & (length - 1);
		}

		return (hash & 0x7FFFFFFF) % length;
	}
}
