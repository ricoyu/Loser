package com.loserico.proxy;

public class Loser implements Human {

	private String name;

	public Loser(String name) {
		this.name = name;
	}

	@Override
	public void say(String msg) {
		System.out.println(msg);
	}
	
	
}
