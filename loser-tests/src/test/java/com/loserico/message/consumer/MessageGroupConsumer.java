package com.loserico.message.consumer;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageGroupConsumer {

	private static final Logger logger = LoggerFactory.getLogger(MessageGroupSender.class);

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		connection.start();

		Destination destination = session.createQueue("msg-group-queue");
		for (int i = 0; i < 2; i++) {
			int index = i;
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener((message) -> {
				TextMessage textMessage = (TextMessage) message;
				try {
					System.out.println("消费者" + index + " 收到消息: " + textMessage.getText());
				} catch (JMSException e) {
					logger.error("msg", e);
				}
			});
		}
	}
}
