package com.loserico.message;

import java.io.File;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;

public class BlobMsgSend {
	public static void main(String[] args)throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
				"tcp://192.168.1.3:61616?jms.blobTransferPolicy.uploadUrl=http://192.168.1.106:8161/fileserver/"
				);
		
		Connection connection = connectionFactory.createConnection();
		connection.start();

		ActiveMQSession session =(ActiveMQSession) connection.createSession(Boolean.TRUE,
				Session.CLIENT_ACKNOWLEDGE);
		
		Destination destination = session.createQueue("my-queue");
		
		
		MessageProducer producer = session.createProducer(destination);
		
		BlobMessage bm = session.createBlobMessage(new File("pom.xml"));
		
		producer.send(bm);
		
		session.commit();
		session.close();
		connection.close();
	}
}
