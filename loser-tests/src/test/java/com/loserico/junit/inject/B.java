package com.loserico.junit.inject;

public class B {

	private A rico;
	
	public void sayHello() {
		System.out.println("Hello " + rico.getName());
	}
}
