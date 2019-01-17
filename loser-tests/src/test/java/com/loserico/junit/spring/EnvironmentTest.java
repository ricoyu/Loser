package com.loserico.junit.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Create a test class to load the message bean from the application context and
 * assert the bean value with I'm the king:
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyConfig.class)
public class EnvironmentTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void environment() throws Exception {
		assertEquals("I'm the king", context.getBean("message"));
	}
}