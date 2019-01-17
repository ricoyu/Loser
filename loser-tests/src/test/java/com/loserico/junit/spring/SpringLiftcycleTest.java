package com.loserico.junit.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringLiftcycleTest {

	@Test
	public void testLifecycle() {
		System.out.println("Spring容器初始化");
		System.out.println("=====================================");

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"com/loserico/commons/junit/spring/lifecycle-context.xml");

		System.out.println("Spring容器初始化完毕");
		System.out.println("=====================================");

		System.out.println("从容器中获取Bean");

		LoserService service = context.getBean("loserService", LoserService.class);

		System.out.println("Loser Name=" + service.getName());
		System.out.println("=====================================");

		context.close();

		System.out.println("Spring容器关闭");

	}
}
