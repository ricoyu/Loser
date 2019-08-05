package com.loserico.commons.utils;

import java.util.UUID;

import org.junit.Test;

public class BinaryUtilsTest {

	@Test
	public void testHex2BinaryStr() {
		String binary = BinaryUtils.hex2BinaryStr("0x7FFFFFFF");
		System.out.println(binary);
	}
	
	@Test
	public void testJDK7Hash() {
		for (int i = 0; i < 100; i++) {
			String obj = UUID.randomUUID().toString();
			int jdk8Hash = BinaryUtils.hash8(obj);
			int jdk7Hash = BinaryUtils.hash7(obj);
//			System.out.println("obj:" + obj +" hashCode:" + obj.hashCode() +" JDK7 HASH:" + jdk7Hash);
//			System.out.println("obj:" + obj +" hashCode:" + obj.hashCode() +" JDK8 HASH:" + jdk8Hash);
			
			int jdk8Index = BinaryUtils.indexFor(jdk8Hash, 7);
			int jdk7Index = BinaryUtils.indexFor(jdk7Hash, 7);
//			System.out.println("奇数 jdk8 index: " + jdk8Index);
			System.out.println("奇数 jdk7 index: " + jdk7Index);
			
			int jdk8Index2 = BinaryUtils.indexFor(jdk8Hash, 4);
			int jdk7Index2 = BinaryUtils.indexFor(jdk7Hash, 4);
//			System.out.println("偶数 jdk8 index: " + jdk8Index2);
//			System.out.println("偶数 jdk7 index: " + jdk7Index2);
		}
	}
}
