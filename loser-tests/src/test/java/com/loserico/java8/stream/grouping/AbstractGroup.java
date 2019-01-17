package com.loserico.java8.stream.grouping;

import java.util.Arrays;
import java.util.List;

import com.loserico.java8.stream.grouping.model.Person;
import com.loserico.java8.stream.grouping.model.Pet;


public abstract class AbstractGroup {

	protected static List<Person> buildPersonsList() {
		Person person1 = new Person("John", "USA", "NYC", new Pet("Max", 5));
		Person person2 = new Person("Steve", "UK", "London", new Pet("Lucy", 8));
		Person person3 = new Person("Anna", "USA", "NYC", new Pet("Buddy", 12));
		Person person4 = new Person("Mike", "USA", "Chicago", new Pet("Duke", 10));

		return Arrays.asList(person1, person2, person3, person4);
	}
}