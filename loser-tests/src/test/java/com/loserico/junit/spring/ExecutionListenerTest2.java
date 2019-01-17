package com.loserico.junit.spring;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

/**
 * If a custom TestExecutionListener class is registered
 * via @TestExecutionListeners, the default listeners will not be registered. This
 * forces the developer to manually declare all default listeners in addition to any
 * custom listeners.
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@ContextConfiguration
@TestExecutionListeners({
		ExecutionListenerSysOutTest.class,
		ServletTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		SqlScriptsTestExecutionListener.class
})
public class ExecutionListenerTest2 {
}