package com.loserico.guava;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterTest {

	private static int size = 1000000;

	private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, 0.01); //误判率设为0.01，底层维护的bit数组长度会增加
	//private static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size);

	/**
	 * 测试一个元素是否属于一个百万元素集合所需耗时
	 */
	@Test
	public void testMillionElem() {
		for (int i = 0; i < size; i++) {
			bloomFilter.put(i);
		}

		long startTime = System.nanoTime(); // 获取开始时间
		//判断这一百万个数中是否包含29999这个数
		if (bloomFilter.mightContain(29999)) {
			System.out.println("命中了");
		}
		long endTime = System.nanoTime(); // 获取结束时间
		/*
		 * 1秒 = 1 000 000 000 纳秒
		 * 程序运行时间： 140246纳秒
		 * 140246纳秒 =0.140246 毫秒
		 * 也就是说，判断一个数是否属于一个百万级别的集合，只要0.140246ms就可以完成，性能极佳。
		 * @on
		 */
		System.out.println("程序运行时间： " + (endTime - startTime) + "纳秒");
	}

	/**
	 * 误判率测试
	 * 
	 * 我们故意取10000个不在过滤器里的值，却还有330个被认为在过滤器里，这说明了误判率为0.03.即，在不做任何设置的情况下，默认的误判率为0.03
	 * 看源码可以知道这是设置的默认值
	 *  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions) {
	 *    return create(funnel, expectedInsertions, 0.03); // FYI, for 3%, we always get 5 hash functions
	 *  }
	 * @on
	 */
	@Test
	public void testErrorRate() {
		for (int i = 0; i < size; i++) {
			bloomFilter.put(i);
		}
		List<Integer> list = new ArrayList<Integer>(1000);
		//故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里
		for (int i = size + 10000; i < size + 20000; i++) {
			if (bloomFilter.mightContain(i)) {
				list.add(i);
			}
		}
		System.out.println("误判的数量：" + list.size()); //误判的数量：330
	}
}
