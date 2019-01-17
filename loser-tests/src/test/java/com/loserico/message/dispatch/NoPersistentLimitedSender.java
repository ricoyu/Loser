package com.loserico.message.dispatch;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoPersistentLimitedSender {

	private static String url = "tcp://192.168.1.3:61616";
	private static final Logger logger = LoggerFactory.getLogger(NoPersistentReceiver.class);

	public static void main(String[] args) throws JMSException {
		System.out.println("messagssssssss".getBytes().length);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("limit.10b");
		MessageProducer producer = session.createProducer(destination);
		
		for (int i = 0; i < 1; i++) {
			String message = "messagssssssss";
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
		}
//		session.commit();
		session.close();
		connection.close();
	}
}
