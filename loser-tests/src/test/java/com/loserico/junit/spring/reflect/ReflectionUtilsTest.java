package com.loserico.junit.spring.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class ReflectionUtilsTest {
/*	
	@Test
	public void privateFieldAccess() throws Exception {
		Secret myClass = new Secret();
		myClass.initiate("aio");
		Field secretField = ReflectionUtils.findField(Secret.class, "secret", String.class);
		assertNotNull(secretField);
		
		ReflectionUtils.makeAccessible(secretField);
		assertEquals("zko", ReflectionUtils.getField(secretField, myClass));
		
		ReflectionUtils.setField(secretField, myClass, "cool");
		assertEquals("cool", ReflectionUtils.getField(secretField, myClass));
	}*/
}