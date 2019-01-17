package com.loserico.message.staticnetwork;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


public class QueueSender {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
//				"tcp://192.168.1.3:61616");
				"failover:(tcp://192.168.1.3:61616,tcp://192.168.1.106:61776)?randomize=false");
		
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(Boolean.TRUE,
				Session.CLIENT_ACKNOWLEDGE);
		
		Destination destination = session.createQueue("MY.QUEUE");
		
		
		MessageProducer producer = session.createProducer(destination);
		
		for (int i = 0; i < 3; i++) {
			TextMessage message = session.createTextMessage("messageCC--" + i);
//			MapMessage message = session.createMapMessage();
//			message.setStringProperty("extra"+i, "okok");
//
//					
//			message.setString("message---"+i, "my map message AAA=="+i);
			
			//			Thread.sleep(1000);
			// 通过消息生产者发出消息
			producer.send(message);
		}
		
		session.commit();
		session.close();
		connection.close();
	}
}