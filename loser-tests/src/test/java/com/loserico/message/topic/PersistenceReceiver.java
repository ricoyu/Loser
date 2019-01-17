package com.loserico.message.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;

public class PersistenceReceiver {
	public static void main(String[] args) throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory(
				"tcp://192.168.1.3:61616");
		
		
		Connection connection = cf.createConnection();
		connection.setClientID("cc1");
		
		final Session session = connection.createSession(Boolean.TRUE,
				Session.AUTO_ACKNOWLEDGE);
		
		Topic topic = session.createTopic("MyTopic4");

		TopicSubscriber consumer = session.createDurableSubscriber(topic, "t1");
		
		connection.start();
		
		Message message = consumer.receive();
	    while(message!=null) {  
	        TextMessage txtMsg = (TextMessage)message;  
	        System.out.println("收到消 息：" + txtMsg.getText());        
	        message = consumer.receive(1000L);
	    } 
	    session.commit();
		session.close();
		connection.close();
	}

}
