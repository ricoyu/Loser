package com.loserico.miscellaneous.init;

class A {

	public A() {
		System.out.println("A的构造函数");
	}
	
	{
		System.out.println("A的构造代码块");
	}
	static {
		System.out.println("A的静态代码块");
	}
}


class B {

	public B() {
		System.out.println("B的构造函数");
	}
	
	{
		System.out.println("B的构造代码块");
	}
	static {
		System.out.println("B的静态代码块");
	}
}

public class Solution {
	public static void main(String[] args) {
		A a = new A();
		B b = new B();
	}
}
