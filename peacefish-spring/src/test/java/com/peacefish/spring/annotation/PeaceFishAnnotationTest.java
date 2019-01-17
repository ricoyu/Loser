package com.peacefish.spring.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:annotation-scan.xml")
public class PeaceFishAnnotationTest {

	@Test
	public void testAnnotationScan() {
		
	}
}
