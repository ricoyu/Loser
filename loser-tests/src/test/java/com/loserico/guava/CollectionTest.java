package com.loserico.guava;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CollectionTest {

	@Test
	public void testCollectionCreate() {
		//Guava提供了能够推断范型的静态工厂方法
		List<String> lists = Lists.newArrayList();
		Map<String, LocalDateTime> map = Maps.newHashMap();
		
		//用工厂方法模式，我们可以方便地在初始化时就指定起始元素
		List<String> names = Lists.newArrayList("rico", "vivi");
		
		//此外，通过为工厂方法命名（Effective Java第一条），我们可以提高集合初始化大小的可读性
		List<String> exactly100 = Lists.newArrayListWithCapacity(100);
		List<String> approx100 = Lists.newArrayListWithExpectedSize(100);
		Set<String> approx100Set = Sets.newHashSetWithExpectedSize(100);
	}
}
