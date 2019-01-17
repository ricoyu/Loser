package com.loserico.junit.inject;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InjectTest {

	@Mock
	private A rico;

	@InjectMocks
	private B client;
	
	@Before
	public void setup() {
		client = new B();
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testSayHello() {
		when(rico.getName()).thenReturn("你好rico");
		client.sayHello();
	}
}
