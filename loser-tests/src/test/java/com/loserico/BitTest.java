package com.loserico;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitTest {

	@Test
	public void testToBit() {
		System.out.println(Integer.toBinaryString(3));
		
	}
	
	@Test
	public void testbit2Int() {
		System.out.println(Integer.valueOf("11", 2));
		System.out.println(3<<2);
		System.out.println(1>>>2);
	}
}
