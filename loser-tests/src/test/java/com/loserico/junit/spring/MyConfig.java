package com.loserico.junit.spring;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * The @PropertySource annotation takes the properties' filenames and sets the
 * properties to the Environment resource.
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@Configuration
@PropertySource({ "classpath:myProp.properties" })
public class MyConfig {
	@Resource
	private Environment environment;

	@Bean(name = "message")
	public String getMessage() {
		return environment.getProperty("message");
	}
}