package com.loserico.junit;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Asa {

	class A {
		public String sayHello() {
			return "hello";
		}
	}

	@Test
	public void testSpy() {
		A date = mock(A.class);
		System.out.println(date.sayHello());
		given(date.sayHello()).willCallRealMethod();
		System.out.println(date.sayHello());
	}
}
