package com.loserico.message.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class NoPerSender2 {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"tcp://192.168.1.3:61616");
		
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(Boolean.TRUE,
				Session.AUTO_ACKNOWLEDGE);
		
		Destination destination = session.createTopic("MyTopic");
		for(int i=0;i<2;i++){
			MessageProducer producer = session.createProducer(destination);
			
			for (int j = 0; j < 3; j++) {
				TextMessage message = session.createTextMessage(i+"==message222--" + j);
				
				// 通过消息生产者发出消息
				producer.send(message);
			}
			session.commit();
		}		
		session.close();
		connection.close();
	}
}