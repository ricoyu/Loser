package com.loserico.methodref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MethodRefCompare {

	public static class Person {
		private String name;
		private int age;

		public Person() {
		}

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return "Name:[" + name + "]" + ", age:[" + age + "]";
		}

	}

	public static void main(String[] args) {
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("vivi", 32));
		persons.add(new Person("zaizai", 3));
		persons.add(new Person("rico", 33));
		Person[] personsArr = persons.toArray(new Person[persons.size()]);

//		Comparator<Person> byName = Comparator.comparing(p -> p.getName());
		Comparator<Person> byName = Comparator.comparing(Person::getName);
		Arrays.sort(personsArr, byName);
		for (int i = 0; i < personsArr.length; i++) {
			Person person = personsArr[i];
			System.out.println(person);
		}
	}
}
