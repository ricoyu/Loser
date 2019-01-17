package com.loserico.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.jsonpath.internal.function.text.Length;

/**
 * https://dzone.com/articles/concurrenthashmap-in-java8
 * 
 * Java 8 introduced the forEach, search, and reduce methods, which are pretty much to support parallelism. 
 * These three operations are available in four forms: accepting functions with keys, values, entries, and key-value pair arguments.
 * 
 * All of those methods take a first argument called parallelismThreshold.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-01-19 10:57
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ConcurrentHashMapTest {

	private static ConcurrentHashMap<String, Integer> hashMap;

	@BeforeClass
	public static void prepareData() {
		hashMap = new ConcurrentHashMap<>();
		hashMap.put("A", 1);
		hashMap.put("B", 2);
		hashMap.put("C", 3);
		hashMap.put("D", 4);
		hashMap.put("E", 5);
		hashMap.put("F", 6);
		hashMap.put("G", 7);
	}

	/**
	 * This is to define how you wanted to execute the operations — sequentially or in
	 * parallel. Suppose you have given a parallelismThreshold as 2. So as long as
	 * there are fewer than two elements in your map, it would be sequential.
	 * Otherwise, it's parallel (depends on the JVM).
	 * 
	 * It produced the below o/p on my machine (you can see two different threads in
	 * action — main and ForkJoinPool.commonPool-worker-1):
	 * key->Ais related with value-> 1, by thread-> main
	 * key->Bis related with value-> 2, by thread-> main
	 * key->Cis related with value-> 3, by thread-> main
	 * key->Dis related with value-> 4, by thread-> ForkJoinPool.commonPool-worker-2
	 * key->Eis related with value-> 5, by thread-> ForkJoinPool.commonPool-worker-2
	 * key->Fis related with value-> 6, by thread-> ForkJoinPool.commonPool-worker-2
	 * key->Gis related with value-> 7, by thread-> ForkJoinPool.commonPool-worker-2
	 * 
	 * @on
	 * 
	 */
	@Test
	public void testParallelismThreshold() {
		hashMap.forEach(2, (k, v) -> {
			System.out.println(
					"key->" + k + "is related with value-> " + v + ", by thread-> " + Thread.currentThread().getName());
		});
	}

	/**
	 * ConcurrentHashMap is all about concurrency and parallelism. 
	 * So it shouldn’t be of any surprise that you’ll find ways of controlling how this parallelism should work.
	 * 	
	 * Through an extra parameter — parallelism threshold — we can say how many elements are needed for an operation to be executed in parallel.
	 * The parallelism threshold is available in most of the new methods — including forEach.
	 * 
	 * In this example, the method will run in parallel when the size of the map reaches the parallelism threshold being 2.
	 * 
	 * There are also a versions of the forEach where we can add a transformer.
	 * The transformer transforms the data before sending it to the Consumer — pretty much like executing a map function on the key-value pair before passing it along.
	 */
	@Test
	public void testForEach() {
		ConcurrentHashMap<String, Integer> forEachMap = new ConcurrentHashMap<>();
		forEachMap.put("One", 1);
		forEachMap.put("Two", 2);
		forEachMap.put("Three", 3);
		forEachMap.put("Four", 4);
		System.out.println("forEachMap1: " + forEachMap);
		System.out.println();

		forEachMap.forEach(2,
				(k, v) -> System.out.println(k + " : " + v + ", by thread-> " + Thread.currentThread().getName()));
		System.out.println("forEachMap2: " + forEachMap);
		System.out.println();

		forEachMap.forEach(2, (k, v) -> "There is " + k + " : " + v, System.out::println);
		System.out.println("forEachMap3: " + forEachMap);
		System.out.println();
	}

	@Test
	public void testBiConsumer() {
		BiConsumer<String, String> biConsumer = (x, y) -> {
			System.out.println("Key => " + x + ", and value => " + y);
		};
		biConsumer.accept("k", "arun");
	}

	/**
	 * The idea behind the search method is that you provide a search function to find a key-value pair.
	 * 
	 * The function you provide not only define what's a qualified key-value pair, but also the result that will be returned.
	 * If the current key-value pair doesn't qualify, the provided function needs to return null to make the search continue.
	 * So what if no pair qualify? Well, then the search will simply return null.
	 */
	@Test
	public void testSearch() {
		String result = hashMap.search(1, (k, v) -> {
			System.out.println(Thread.currentThread().getName());
			if (k.equals("A")) {
				return k + "-" + v;
			}
			return null;
		});

		System.out.println("result => " + result);
	}
	
	/**
	 * Searching based on the keys
	 * 
	 * taking a function with only the key as a parameter.
	 */
	@Test
	public void testSearchKeys() {
		String result = hashMap.searchKeys(2, (k) -> {
			System.out.println(Thread.currentThread().getName());
			if(k.equals("A")) {
				return k;
			} else {
				return null;
			}
		});
		System.out.println(result);
	}
	
	/**
	 * Searching based on the values
	 * 
	 */
	@Test
	public void testSearchValues() {
		Integer result = hashMap.searchValues(2, (v) -> v > 3 ? v : null);
		System.out.println(result);
	}

	/**
	 * The method signature of the merge method is:
	 * 		public V merge(K key, V value, BiFunction remappingFunction)
	 * 
	 * Here, remappingFunction is the function that recomputes a value if present.
	 * 
	 * If the specified key is not already associated with a (non-null) value, associates it with the given value. 
	 * Otherwise, replaces the value with the results of the given remapping function, or removes if null. 
	 * The entire method invocation is performed atomically. 
	 * map.merge("X", "x1", remappingFunction)
	 * map中的键X没有绑定value，那么put("X", "x1")
	 * map中的键X已经有值了，             那么put("X", remappingFunction)
	 * 
	 * Some attempted update operations on this map by other threads may be blocked while computation is in progress, 
	 * so the computation should be short and simple, and must not attempt to update any other mappings of this Map.
	 * @on
	 */
	@Test
	public void testMerge() {
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		map.put("X", "x");
		System.out.println("1st ==> " + map);
		System.out.println("2nd ==> " + map.merge("X", "x", (v1, v2) -> null));
		System.out.println("3rd ==> " + map);

		map.put("Y", "y");
		map.put("X", "x1");

		System.out.println("4th ==> " + map.merge("X", "x2", (v1, v2) -> {
			return "z";
		}));
		System.out.println("5th ==> " + map);
		System.out.println("6th ==> " + map.merge("X", "x1", (v1, v2) -> {
			return v1.concat(v2);
		}));
		System.out.println("7th ==> " + map);
	}

	/**
	 * 如果key有绑定的值则返回之，否则当给定的默认值
	 */
	@Test
	public void testGetOrDefault() {
		ConcurrentHashMap<String, Integer> defaultMap = new ConcurrentHashMap<String, Integer>();
		defaultMap.put("X", 30);
		System.out.println(defaultMap);
		System.out.println(defaultMap.getOrDefault("Y", 21));
	}

	/**
	 * Generally, we do some computation on map values and store it back. In the
	 * concurrent model, it's difficult to manage, and that's the reason Java
	 * introduced the compute method. The entire method invocation is performed
	 * atomically. compute方法根据key/value重新计算value，然后put back，原子操作
	 * 
	 * The compute and computeIfPresent methods take a remapping function as an
	 * argument to compute a value, and remapping is of type BiFunction. The
	 * computeIfAbsent method takes an argument as mappingFunction to compute a value,
	 * and hence mappingFunction is of type Function.
	 */
	@Test
	public void testCompute() {
		ConcurrentHashMap<String, Integer> map1 = new ConcurrentHashMap<>();
		map1.put("A", 1);
		map1.put("B", 2);
		map1.put("C", 3);

		// Compute a new value for the existing key
		System.out.println("1st print => " + map1.compute("A", (k, v) -> {
			return v == null ? 42 : v + 40;
		}));
		System.out.println("2nd print => " + map1);

		// This will add a new (key, value) pair
		System.out.println("3rd print => " + map1.compute("X", (k, v) -> {
			return v == null ? 42 : v + 41;
		}));
		System.out.println("4th print => " + map1);

		//computeIfPresent method
		System.out.println("5th print => " + map1.computeIfPresent("X", (k, v) -> {
			return v == null ? 42 : v + 10;
		}));
		System.out.println("6th print => " + map1);

		//computeIfAbsent method
		System.out.println("7th print => " + map1.computeIfAbsent("Y", (k) -> 90));
		System.out.println("8th print => " + map1);
	}

	@Test
	public void testPutIfAbsent() {
		ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
		map.putIfAbsent("ricoyu", "handsome");
		map.putIfAbsent("ricoyu", "very handsome");
		assertEquals(map.get("ricoyu"), "handsome");
	}

	/**
	 * Accumulating the data in a map
	 * 方法签名： public U reduce(long parallelismThreshold, BiFunction transformer,
	 * BiFunction reducer)
	 * 
	 * reduce方法有很多变种
	 * 				阀值		Transformer			reducer
	 * 	map.reduce(100, (k, v) -> v.size(), (total, elem) -> total + elem);  
	 * 
	 * Reduce the keys
	 *  map.reduceKeys(1, (k1, k2) -> k1.compareTo(k2) < 0 ? k1 : k2);  
	 * 
	 * Reduce the values
	 * 	map.reduceValues(1, List::size, (total, elem) -> total + elem);
	 * 
	 * Here, transformer is a function returning the transformation for an element, or
	 * null if there is no transformation (in which case it is not combined), and
	 * reducer is a commutative associative combining function.
	 * 
	 * The reduce method calls the MapReduceMappingsTask's invoke method.
	 * MapReduceMappingsTask extends BulkTask, and we have seen this already.
	 * 
	 * reduceKeys方法的reducer接收的k1, k2先取首尾两个key、再取第二个和倒数第二个key……
	 */
	@Test
	public void testReduce() {
		ConcurrentHashMap<String, Integer> reducedMap = new ConcurrentHashMap<>();
		reducedMap.put("One", 1);
		reducedMap.put("Two", 2);
		reducedMap.put("Three", 3);
		reducedMap.put("Four", 4);
		System.out.println("reduce example => " +
				reducedMap.reduce(2, (k, v) -> v * 2, (total, elem) -> total + elem));

		System.out.println("reduceKeys example => " +
				reducedMap.reduceKeys(2, (k1, k2) -> {
					return k1.length() > k2.length() ? k1 + "-" + k2 : k2 + "-" + k1;
				}));
		
		System.out.println("reduceValues example => " + 
				reducedMap.reduceValues(2, (v) -> v, (total, elem) -> total + elem));
	}

}
