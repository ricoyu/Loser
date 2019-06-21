package com.loserico.generic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class JavaGenericAndCollectionTest {

	@Test
	public void test1() {
		List<String> words = new ArrayList<>();
		words.add("Hello ");
		words.add("world!");
		String s = words.get(0) + words.get(1);
		assertEquals("Hello world!", s);
	}

	/**
	 * Subtyping is transitive, meaning that if one type is a subtype of a second,
	 * and the second is a subtype of a third, then the first is a subtype of the
	 * third. So, from the last two lines in the preceding list, it follows that
	 * List<E> is a subtype of Iterable<E>. If one type is a subtype of another, we
	 * also say that the second is a supertype of the first. Every reference type is
	 * a subtype of Object, and Object is a supertype of every reference type.
	 * 
	 * The Substitution Principle tells us that wherever a value of one type is
	 * expected, one may provide a value of any subtype of that type:
	 * 
	 * Substitution Principle: a variable of a given type may be assigned a value of
	 * any subtype of that type, and a method with a parameter of a given type may
	 * be invoked with an argument of any subtype of that type.
	 */
	@Test
	public void testSubTypingSubstitution() {
		/*
		 * According to the Substitution Principle, if we have a collection of numbers,
		 * we may add an integer or a double to it, because Integer and Double are
		 * subtypes of Number.
		 */
		List<Number> numbers = new ArrayList<>();
		numbers.add(2);
		numbers.add(3.14);
		assertEquals("[2, 3.14]", numbers.toString());
		System.out.println(numbers.toString());
	}

	/**
	 * It may seem reasonable to expect that since Integer is a subtype of Number,
	 * it follows that List<Integer> is a subtype of List<Number>. But this is not
	 * the case, because the Substitution Principle would rapidly get us into
	 * trouble. It is not always safe to assign a value of type List<Integer> to a
	 * variable of type List<Number>. Consider the following code fragment:
	 */
	@Test
	public void testCollectionSubType() {
		List<Integer> ints = new ArrayList<>();
		ints.add(1);
		ints.add(2);

		/*
		 * This code assigns variable ints to point at a list of integers, and then
		 * assigns nums to point at the same list of integers; hence the call in the
		 * fifth line adds a double to this list, as shown in the last line. This must
		 * not be allowed! The problem is prevented by observing that here the
		 * Substitution Principle does not apply: the assignment on the fourth line is
		 * not allowed because List<Integer> is not a subtype of List<Number>, and the
		 * compiler reports that the fourth line is in error. 
		 * 
		 * 注意: Integer 是 Number的子类, 但是List<Integer> 并不是 List<Number> 的子类
		 * 所以下面赋值会编译错误
		 */
		// List<Number> nums = ints;

		// 当然, 反过来也不行
		List<Number> nums = new ArrayList<>();
		nums.add(2.78);
		nums.add(3.14);
		/*
		 * The problem is prevented by observing that here the Substitution Principle
		 * does not apply: the assignment on the fourth line is not allowed because
		 * List<Number> is not a subtype of List<Integer>, and the compiler reports that
		 * the fourth line is in error.
		 */
		// List<Integer> ints = nums;

		/*
		 * So List<Integer> is not a subtype of List<Number>, nor is List<Number> a
		 * subtype of List<Integer>; all we have is the trivial case, where
		 * List<Integer> is a subtype of itself, and we also have that List<Integer> is
		 * a subtype of Collection<Integer>.
		 */
		Collection<Number> colls = nums;
	}

	/**
	 * The quizzical phrase "? extends E" means that it is also OK to add all
	 * members of a collection with elements of any type that is a subtype of E. The
	 * question mark is called a wildcard, since it stands for some type that is a
	 * subtype of E
	 * 
	 * Here is an example. We create an empty list of numbers, and add to it first a
	 * list of integers and then a list of doubles:
	 */
	@Test
	public void testWildcardAndExtends() {
		List<Number> nums = new ArrayList<>();
		List<Integer> ints = Arrays.asList(1, 2);
		List<Double> dbls = Arrays.asList(2.78, 3.14);

		/*
		 * The first call is permitted because nums has type List<Number>, which is a
		 * subtype of Collection<Number>, and ints has type List<Integer>, which is a
		 * subtype of Collection<? extends Number>.
		 * 
		 * 方法签名 
		 * boolean addAll(Collection<? extends E> c); 
		 * 
		 * If the method signature for addAll had been written without the wildcard,
		 * then the calls to add lists of integers and doubles to a list of numbers
		 * would not have been permitted; you would only have been able to add a list
		 * that was explicitly declared to be a list of numbers.
		 */
		nums.addAll(ints);
		nums.addAll(dbls);
		assertEquals("[1, 2, 2.78, 3.14]", nums.toString());
		System.out.println(nums.toString());
	}

	/**
	 * We can also use wildcards when declaring variables. Here is a variant of the
	 * example at the end of the preceding section, changed by adding a wildcard to
	 * the second line:
	 */
	@Test
	public void testWildcardAndExtends2() {
		List<Integer> ints = new ArrayList<>();
		ints.add(1);
		ints.add(2);

		List<? extends Number> nums = ints;

		/*
		 * but the fifth line causes a compile-time error (because you cannot add a
		 * double to a List<? extends Number>, since it might be a list of some other
		 * subtype of number).
		 * 
		 * 这种情况可以赋值, 但是不可以往里面添加任何元素
		 */
		// nums.add(3.14);
		// nums.add(1);
		nums.clear();
		nums.remove(0);
		/*
		 * In general, if a structure contains elements with a type of the form ?
		 * extends E, we can get elements out of the structure, but we cannot put
		 * elements into the structure. To put elements into the structure we need
		 * another kind of wildcard, as explained in the next section.
		 */
	}

	/**
	 * The quizzical phrase ? super T means that the destination list may have
	 * elements of any type that is a supertype of T, just as the source list may
	 * have elements of any type that is a subtype of T.
	 */
	@Test
	public void testWildcardAndSuper() {
		List<Object> objs = Arrays.<Object>asList(2, 3.14, "four");
		List<Integer> ints = Arrays.asList(5, 6);
		Collections.copy(objs, ints);
		assertEquals("[5, 6, four]", objs.toString());
		
		/*
		 * The first call leaves the type parameter implicit; 
		 * it is taken to be Integer, since that is the most specific choice that works.
		 */
		Collections.<Object>copy(objs, ints);
		Collections.<Number>copy(objs, ints);
		Collections.<Integer>copy(objs, ints);
	}
}
