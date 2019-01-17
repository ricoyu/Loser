package com.loserico.methodref;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Test;

import com.loserico.methodref.example.Person;

public class MethodRefTest {

	@Test
	public void test1() {

		/*
		 * a reference to an instance method of a particular object
		 */
		class ComparisonProvider {
			public int compareByName(Person a, Person b) {
				return a.getName().compareTo(b.getName());
			}

			@SuppressWarnings("unused")
			public int compareByAge(Person a, Person b) {
				return a.getBirthday().compareTo(b.getBirthday());
			}
		}
		ComparisonProvider myComparisonProvider = new ComparisonProvider();
		List<Person> roster = Person.createRoster();
		Person[] rosterAsArray = roster.toArray(new Person[roster.size()]);
		/*
		 * The method reference myComparisonProvider::compareByName invokes the
		 * method compareByName that is part of the object
		 * myComparisonProvider. The JRE infers the method type arguments,
		 * which in this case are (Person, Person).
		 */
		Arrays.sort(rosterAsArray, myComparisonProvider::compareByName);
	}

	/**
	 * You can reference a constructor in the same way as a static method by
	 * using the name new. The following method copies elements from one
	 * collection to another:
	 * 
	 * @param sourceCollection
	 * @param collectionFactory
	 * @return
	 */
	public static <T, S extends Collection<T>, D extends Collection<T>> D transferElements(S sourceCollection,
			Supplier<D> collectionFactory) {
		D result = collectionFactory.get();
		for (T t : sourceCollection) {
			result.add(t);
		}
		return result;
	}

	/**
	 * The functional interface Supplier contains one method get that takes no
	 * arguments and returns an object. Consequently, you can invoke the method
	 * transferElements with a lambda expression as follows:
	 */
	@Test
	public void testTransferElements() {
		List<Person> roster = Person.createRoster();
		Set<Person> rosterSetLambda = transferElements(roster, () -> {
			return new HashSet<>();
		});
		rosterSetLambda.stream().map(p -> p.getName()).forEach(System.out::println);
	}

	@Test
	public void testArbitraryObject() {
		String[] stringArray = { "Barbara", "James", "Mary", "John", "Patricia", "Robert", "Michael", "Linda" };
		Stream.of(stringArray).forEach(System.out::println);
		System.out.println("===============================");
		Arrays.sort(stringArray, String::compareToIgnoreCase);
		Stream.of(stringArray).forEach(System.out::println);
	}
}
