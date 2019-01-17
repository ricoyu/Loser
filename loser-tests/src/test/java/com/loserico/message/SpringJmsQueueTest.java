package com.loserico.message;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jms-queue-config.xml")
public class SpringJmsQueueTest {

	private static final Logger logger = LoggerFactory.getLogger(SpringJmsQueueTest.class);

	@Resource
	private JmsTemplate jmsTemplate;

	/*
	 * 发送到默认的queue
	 */
	@Test
	public void testSpringJmsSend() {
		/*jmsTemplate.send((session) -> {
			logger.info("发送一条Spring msg...");
			return session.createTextMessage("Spring msg...");
		});*/
	}
	
	@Test
	public void testSpringJmsReceive() {
		logger.info((String)jmsTemplate.receiveAndConvert());
	}
}
