package com.loserico.message.topic;

import static java.lang.Boolean.TRUE;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class NoPersistenceReceiver {
	
	public static void main(String[] args) throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		
		Connection connection = cf.createConnection();
		connection.start();
		
		final Session session = connection.createSession(TRUE, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("MyTopic");
		
		MessageConsumer consumer = session.createConsumer(destination);
		Message message = consumer.receive();
	    while(message!=null) {  
	        TextMessage txtMsg = (TextMessage)message;  
	        System.out.println("收到消 息：" + txtMsg.getText());        
	        message = consumer.receive();
	    } 
	    session.commit();
		session.close();
		connection.close();
	}

}
