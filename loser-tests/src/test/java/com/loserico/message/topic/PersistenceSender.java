package com.loserico.message.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class PersistenceSender {
	public static void main(String[] args) throws Exception {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"tcp://192.168.1.3:61616");

		Connection connection = connectionFactory.createConnection();
		

		Session session = connection.createSession(Boolean.TRUE,
				Session.AUTO_ACKNOWLEDGE);
		
		Destination destination = session.createTopic("MyTopic4");
		
		MessageProducer producer = session.createProducer(destination);

		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		
		connection.start();
		
		for (int i = 0; i < 3; i++) {
			TextMessage message = session.createTextMessage("message333--" + i);
			
			// 通过消息生产者发出消息
			producer.send(message);
		}
		
		session.commit();
		session.close();
		connection.close();
	}
}