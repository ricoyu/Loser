package com.loserico.message;

import static java.util.concurrent.TimeUnit.*;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class MessageReplayTest {

	@Test
	public void testSender3() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("msg-replay-3");

		MessageProducer producer = session.createProducer(destination);
		for (int i = 0; i < 50; i++) {
			String msg = "Message " + i + " from 192.168.1.3";
			System.out.println(msg);
			TextMessage message = session.createTextMessage(msg);
			producer.send(message);
		}
	}
	
	@Test
	public void testReceiver4() throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.4:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("msg-replay-3");
		
		MessageConsumer consumer = session.createConsumer(destination);
		for (int i = 0; i < 50; i++) {
			SECONDS.sleep(1);
			TextMessage message = (TextMessage)consumer.receive();
			System.out.println("从Architect4收到消息: " + message.getText());
		}
	}
	
	@Test
	public void testReceiver3() throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("msg-replay-3");
		
		MessageConsumer consumer = session.createConsumer(destination);
		for (int i = 0; i < 50; i++) {
			SECONDS.sleep(1);
			TextMessage message = (TextMessage)consumer.receive();
			System.out.println("从PeaceFish收到消息: " + message.getText());
		}
	}
}
