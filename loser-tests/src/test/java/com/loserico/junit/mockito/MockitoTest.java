package com.loserico.junit.mockito;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTest {

	@Test
	public void testDelegate() {
		Date date = new Date();
		Date mocked = mock(Date.class, delegatesTo(date));
		System.out.println(mocked.getTime());
	}

	@Test
	public void testMatcher() {
		Date date = mock(Date.class);
	}

	@Test
	public void testException() {
	}

	class A {
		public void throwExp() {
			System.out.println("throw exception");
		}
	}

	@Test
	public void testVoidMethodThrowException() {
		A a = mock(A.class);
		doThrow(new RuntimeException()).when(a).throwExp();
		a.throwExp();
	}
}
