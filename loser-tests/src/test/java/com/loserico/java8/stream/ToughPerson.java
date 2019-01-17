package com.loserico.java8.stream;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

public class ToughPerson {

	private String name;
	private int age;

	public ToughPerson(String name, int age) {
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
		return toJson(this);
	}

	
}