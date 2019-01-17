package com.loserico.message;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.BlobMessage;

public class BlobMsgReceiver {
	public static void main(String[] args) throws Exception{
		ConnectionFactory cf = new ActiveMQConnectionFactory(
				"tcp://192.168.1.3:61616");
		
		Connection connection = cf.createConnection();
		connection.start();
		
		final Session session = connection.createSession(Boolean.FALSE,
				Session.CLIENT_ACKNOWLEDGE);
		Destination destination = session.createQueue("my-queue");

		MessageConsumer consumer = session.createConsumer(destination);
		
		consumer.setMessageListener(new MessageListener() {
			
			public void onMessage(Message msg) {
				if(msg instanceof BlobMessage){
					BlobMessage message = (BlobMessage) msg;
					try {
						InputStream in = message.getInputStream();
						byte bs[] = new byte[in.available()];
						
						in.read(bs);
						in.close();
						
						System.out.println("content===="+new String(bs));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		});
	}
}
