package com.loserico.junit.spring.jndi;

import static org.junit.Assert.*;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * When we run a JUnit test, the container is not accessible; hence, we need to mock
 * out the <jee:jndi-lookup> from our JUnit test. We'll create an
 * ApplicationContextInitializer instance to initialize the application context and
 * bind a mock DataSource object with the original DataSource name.
 * 
 * A SimpleNamingContextBuilder object is created and then a mock DataSource object
 * is bound to the name java:comp/env/Datasource; finally, the builder is activated
 * in the ApplicationContextInitializer interface.
 * 
 * @author Loser
 * @since Aug 9, 2016
 * @version
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:com/loserico/commons/junit/spring/jndi/applicationContext.xml", initializers = DataSourceTest.MockJeeLookUpInitializer.class)
public class DataSourceTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void jndiResource() throws Exception {
		assertNotNull(context.getBean("common-Datasource"));
	}

	public static class MockJeeLookUpInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			DataSource mockDataSource = (javax.sql.DataSource) Mockito.mock(javax.sql.DataSource.class);
			SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
			builder.bind("java:comp/env/Datasource", mockDataSource);
			try {
				builder.activate();
			} catch (IllegalStateException | NamingException e) {
				e.printStackTrace();
			}
		}
	}
}