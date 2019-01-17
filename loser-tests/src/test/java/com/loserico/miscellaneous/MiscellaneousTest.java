package com.loserico.miscellaneous;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.junit.Test;

public class MiscellaneousTest {

	@Test
	public void testStringJoiner() {
		StringJoiner joiner = new StringJoiner(", ");
		joiner.add("foo");
		joiner.add("bar");
		joiner.add("baz");
		String joined = joiner.toString(); // "foo,bar,baz"
		System.out.println(joined);

		// add() calls can be chained
		joined = new StringJoiner("-")
				.add("foo")
				.add("bar")
				.add("baz")
				.toString(); // "foo-bar-baz"
		System.out.println(joined);
	}

	@Test
	public void testStringJoiner2() {
		StringJoiner joiner = new StringJoiner(", ", "[", "]");
		joiner.add("foo");
		joiner.add("bar");
		joiner.add("baz");
		String joined = joiner.toString(); // [foo, bar, baz]
		System.out.println(joined);
		assertEquals("[foo, bar, baz]", joiner.toString());
	}

	@Test
	public void testStringJoiner3() {
		// join(CharSequence delimiter, CharSequence... elements)
		String joined = String.join("/", "2014", "10", "28"); // "2014/10/28"
		assertEquals("2014/10/28", joined);

		// join(CharSequence delimiter, Iterable<? extends CharSequence> elements)
		List<String> list = Arrays.asList("foo", "bar", "baz");
		joined = String.join(";", list); // "foo;bar;baz"
		assertEquals("foo;bar;baz", joined);
	}

/*	@Test
	public void testJoin4() {
		List<Person> list = Arrays.asList(
				new Person("John", "Smith"),
				new Person("Anna", "Martinez"),
				new Person("Paul", "Watson "));

		String joinedFirstNames = list.stream()
				.map(Person::getFirstName)
				.collect(Collectors.joining(", ")); // "John, Anna, Paul"
		assertEquals("John, Anna, Paul", joinedFirstNames);
	}*/
}
