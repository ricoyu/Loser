package com.loserico.junit.mockito;

public class VoidMethodClass {

	public void voidMethodThrowingExcetion(boolean check) {
		if (check) {
			throw new IllegalArgumentException();
		}
	}
}
