package com.loserico.concurrent;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class AtomicTest {

	@Test
	public void testAtomicLong() {
		AtomicLong sequenceNumber = new AtomicLong(0);
		for (int i = 0; i < 10; i++) {
			System.out.println(sequenceNumber.getAndIncrement());
		}
	}
}
