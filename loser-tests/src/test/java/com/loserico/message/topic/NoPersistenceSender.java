package com.loserico.message.topic;

import static java.lang.Boolean.TRUE;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

public class NoPersistenceSender {
	
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(TRUE, AUTO_ACKNOWLEDGE);
		
		Destination destination = session.createTopic("MyTopic");
		MessageProducer producer = session.createProducer(destination);
		
		for (int i = 0; i < 3; i++) {
			TextMessage message = session.createTextMessage("message111--" + i);
			
			long delay = 3 * 1000;
			long period = 3 * 1000;
			int repeat = 5;
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
			message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);

			// 通过消息生产者发出消息
			producer.send(message);
		}
		
		session.commit();
		session.close();
		connection.close();
	}
}