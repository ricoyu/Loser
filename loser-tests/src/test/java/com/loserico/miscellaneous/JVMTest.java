package com.loserico.miscellaneous;

import org.junit.Test;

/**
 * JVM System Property
 * https://docs.oracle.com/javase/6/docs/api/java/lang/System.html#getProperties()
 * 
 * <p>
 * Copyright: Copyright (c) 2019-03-03 22:48
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class JVMTest {

	@Test
	public void testJVMVersion() {
		String version = System.getProperty("java.version");
		System.out.println(version);
		System.out.println(System.getProperty("java.vm.specification.version"));
		System.out.println(System.getProperty("sun.arch.data.model") ); // 返回JVM是64位还是32位
	}
}
