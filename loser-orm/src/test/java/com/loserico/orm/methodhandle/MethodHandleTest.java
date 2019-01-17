package com.loserico.orm.methodhandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.math.BigDecimal;

import org.junit.Test;

import com.loserico.orm.convertor.IntConvertor;

public class MethodHandleTest {

	@Test
	public void testMethodType() throws Throwable {
		MethodType bigDecimal2IntType = MethodType.methodType(int.class, BigDecimal.class);
		MethodType Long2Inttype = MethodType.methodType(int.class, Long.class);
		MethodHandle bigDecimal2IntHandle = MethodHandles.lookup().findStatic(IntConvertor.class, "convert", bigDecimal2IntType);
		int result = (int)bigDecimal2IntHandle.invoke(null);;
		System.out.println(result);
	}

}
