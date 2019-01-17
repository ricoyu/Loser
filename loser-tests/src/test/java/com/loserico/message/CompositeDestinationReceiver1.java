package com.loserico.message;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeDestinationReceiver1 {

	private static final Logger logger = LoggerFactory.getLogger(CompositeDestinationReceiver1.class);

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(session.createQueue("multi-queue1"));
		consumer.setMessageListener((message) -> {
			try {
				System.out.println("从multi-queue1收到消息: " + ((TextMessage) message).getText());
			} catch (JMSException e) {
				logger.error("msg", e);
			}
		});
	}
}
