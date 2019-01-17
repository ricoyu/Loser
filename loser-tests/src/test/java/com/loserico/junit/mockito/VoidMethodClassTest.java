package com.loserico.junit.mockito;

import static org.mockito.Mockito.doThrow;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class VoidMethodClassTest {

	private VoidMethodClass mock;

	@Test
	public void testVoidMethodThrowingExcetion() {
		mock = Mockito.mock(VoidMethodClass.class);
		doThrow(new IllegalArgumentException()).when(mock).voidMethodThrowingExcetion(false);
		//这里不会抛IllegalArgumentException，因为mock对象的方法不会真的执行
		mock.voidMethodThrowingExcetion(true);
		Mockito.doThrow(new IllegalArgumentException()).when(mock).voidMethodThrowingExcetion(true);
		try {
			//这里会抛异常
			mock.voidMethodThrowingExcetion(true);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

}
