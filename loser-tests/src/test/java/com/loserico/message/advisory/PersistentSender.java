package com.loserico.message.advisory;

import static javax.jms.DeliveryMode.PERSISTENT;
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

public class PersistentSender {

	private static String url = "tcp://192.168.1.3:61616";
	private static final Logger logger = LoggerFactory.getLogger(PersistentSender.class);

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("topic-advisory");
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(PERSISTENT);
		//start()要延迟到setDeliveryMode(PERSISTENT)之后
		connection.start();

		String msg = "持久化Topic消息";
		TextMessage textMessage = session.createTextMessage(msg);
		System.out.println("发送 " + msg);
		producer.send(textMessage);

		session.close();
		connection.close();
	}
}
