package com.loserico.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyTest {

	private Human human;

	public static void main(String[] args) {
		ProxyTest proxyTest = new ProxyTest();
		proxyTest.setHuman(new Loser("Lower EQ"));
		InvocationHandler invocationHandler = new TraceHandler(proxyTest.getHuman());
		Human proxy = (Human) Proxy.newProxyInstance(proxyTest.getHuman().getClass().getClassLoader(),
				proxyTest.getHuman().getClass().getInterfaces(), invocationHandler);
		proxy.say("Hello");
	}

	public Human getHuman() {
		return human;
	}

	public void setHuman(Human human) {
		this.human = human;
	}
}
