package com.loserico.miscellaneous;

public class InnerClassSingleton {

	private static class InstanceHolder {
		
		private static final InnerClassSingleton instance = new InnerClassSingleton();
		
	}
	
	private InnerClassSingleton() {}
	
	public static InnerClassSingleton getInstance() {
		return InstanceHolder.instance;
	}
}
