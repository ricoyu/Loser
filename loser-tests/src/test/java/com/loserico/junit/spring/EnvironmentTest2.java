package com.loserico.junit.spring;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Suppose we want to mock the properties file reading with a mock value. To mock
 * the Environment value, we need to change the application context's Environment
 * value at the time of context initialization. The @ContextConfiguration annotation
 * takes a ApplicationContextInitializer instance for explicit initialization; we
 * can create a ApplicationContextInitializer instance and change the Environment
 * value of ApplicationContext with a MockEnvironment object.
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyConfig.class, initializers = EnvironmentTest2.MockPropertyInitializer.class)
public class EnvironmentTest2 {
	@Autowired
	ApplicationContext context;

	@Test
	public void environment() throws Exception {
		assertEquals("I'm the king", context.getBean("message"));
	}

	public static class MockPropertyInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			MockEnvironment mock = new MockEnvironment();
			mock.setProperty("message", "I'm a mockstar");
			applicationContext.setEnvironment(mock);
		}
	}
}