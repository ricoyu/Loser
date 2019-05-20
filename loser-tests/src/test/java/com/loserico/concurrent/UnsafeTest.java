package com.loserico.concurrent;

import java.lang.reflect.Field;

import org.junit.Test;

import sun.misc.Unsafe;

public class UnsafeTest {

	@Test
	public void testUnsafe() {
		Unsafe unsafe = Unsafe.getUnsafe();
	}
	
	@Test
	public void testStealUnsafe() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		Unsafe unsafe = (Unsafe) field.get(null);
	}
}
