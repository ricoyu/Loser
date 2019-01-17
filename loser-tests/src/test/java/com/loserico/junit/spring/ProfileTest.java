package com.loserico.junit.spring;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The @ActiveProfiles annotation takes an array of active profile names. We passed
 * the value dev to load the dev profile beans. We asserted the message bean value
 * with I'm a dev bean. Note that the noProfileBean is also loaded with the value
 * I'm a free bean although we asked to load the dev profile. When we define a bean
 * in the absence of a profile name (or just under the default profile) and try to
 * load a specific profile, the bean defined under no profile is also loaded along
 * with the beans with matching profile names.
 * 
 * If we change the @ActiveProfiles annotation to load both the profiles, such
 * as @ActiveProfiles(profiles={"dev", "prod"}), the Spring context loads the last
 * defined bean in the application context, as the prod profile is defined after the
 * dev profile (in applictionContext.xml). So, here it will load the prod profile
 * bean and the test will fail, as the test asserts the dev value with a prod value.
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:com/loserico/commons/junit/spring/applicationContext.xml")
@ActiveProfiles(profiles = { "dev" }) // -Dspring.profiles.active= profile1,
										// profile2 ...
public class ProfileTest {
	@Autowired
	ApplicationContext context;

	@Test
	public void profile() throws Exception {
		assertEquals("I'm a dev bean", context.getBean("message"));
		assertEquals("I'm a free bean", context.getBean("noProfileBean"));
	}
}