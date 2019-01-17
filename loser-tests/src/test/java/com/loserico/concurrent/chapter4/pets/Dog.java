package com.loserico.concurrent.chapter4.pets;

public class Dog extends Pet {
	public Dog(String name) {
		super(name);
	}

	@Override
	public void examine() {
		System.out.println("Woof!");
	}
}
