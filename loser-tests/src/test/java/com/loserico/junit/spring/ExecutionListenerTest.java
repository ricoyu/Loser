package com.loserico.junit.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * By annotating test classes with @RunWith(SpringJUnit4ClassRunner.class), we
 * enable the class to get the benefits of Spring unit and integration tests, such
 * as TestContext, the applicationContext loading, DI, transaction support, and so
 * on.
 * 
 * The @ContextConfiguration annotation loads the application context resource from
 * the specified locations or the @Configuration annotated classes. In locations, we
 * pass the XML configuration or the applicationContext XML location that can be
 * loaded from the classpath.
 * 
 * The @TestExecutionListeners annotation defines class-level metadata to configure
 * which TestExecutionListener implementations should be registered with
 * TestContextManager.
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:com/loserico/commons/junit/spring/applicationContext.xml")
@TestExecutionListeners({ ExecutionListenerSysOutTest.class })
public class ExecutionListenerTest {
	@Test
	public void someTest() throws Exception {
		System.out.println("executing someTest");
	}

	@Test
	public void someOtherTest() throws Exception {
		System.out.println("executing someOtherTest");
	}
}