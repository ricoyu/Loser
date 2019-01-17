package com.loserico.generic;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class GenericTest {

	@Test
	public void test1() throws NoSuchFieldException, SecurityException, NoSuchMethodException {
		ParameterizedType type = (ParameterizedType) Bar.class.getGenericSuperclass();
		System.out.println(type.getActualTypeArguments()[0]);

		ParameterizedType fieldType = (ParameterizedType) Foo.class.getField("children").getGenericType();
		System.out.println(fieldType.getActualTypeArguments()[0]);

		ParameterizedType parameterizedType = (ParameterizedType) Foo.class.getMethod("foo", List.class)
				.getGenericParameterTypes()[0];
		System.out.println(parameterizedType.getActualTypeArguments()[0]);

		System.out.println(Foo.class.getTypeParameters()[0]);
		System.out.println(Foo.class.getTypeParameters()[0].getBounds()[0]);
		/*
		 * 输出：
		 * class java.lang.String
		 * class com.loserico.generic.GenericTest$Bar
		 * class java.lang.String
		 * E
		 * interface java.lang.CharSequence
		 * @on
		 */

		/*
		 * 关于类结构的泛型信息都是保留的，通过反射可以得到
		 * You see that every single type argument is preserved and is accessible via
		 * reflection at runtime. But then what is “type erasure”? Something must be
		 * erased? Yes. In fact, all of them are, except the structural ones –
		 * everything above is related to the structure of the classes, rather than
		 * the program flow. In other words, the metadata about the type arguments of
		 * a class and its field and methods is preserved to be accessed via
		 * reflection.
		 * @on
		 */

		//但是下面这段代码就会被擦除
		List<String> list = new ArrayList<>();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String s = it.next();
		}

		//跟上面那段代码等价
		List list2 = new ArrayList();
		Iterator it2 = list.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
		}

		/*
		 * So, all type arguments you have defined in the bodies of your methods will
		 * be removed and casts will be added where needed. Also, if a method is
		 * defined to accept List<T>, this T will be transformed to Object (or to its
		 * bound, if such is declared. And that’s why you can’t do new T()
		 */
	}

	@Test
	public void testNewArray() {
		Object instance = Array.newInstance(Foo.class, 0);
		System.out.println(instance);
	}
	
	@Test
	public void testTypedList() {
		Class<String> type = String.class;
		List<String> list = populateList(type);
		System.out.println(list);
	}
	
	/**
	 * 因为类型擦除，结果是true
	 */
	@Test
	public void testErase() {
		List<String> l1 = new ArrayList<>();
		List<Integer> l2 = new ArrayList<>();
		
		System.out.println(l1.getClass() == l2.getClass());
	}
	
	static <Type> List<Type> populateList(Class<?> type) {
		List<Type> list = new ArrayList<Type>();
		return list;
	}
	static class Foo<E extends CharSequence> {
		public List<Bar> children = new ArrayList<Bar>();

		public List<StringBuilder> foo(List<String> foo) {
			return null;
		}

		public void bar(List<? extends String> param) {
		}

	}

	static class Bar extends Foo<String> {
	}

}
