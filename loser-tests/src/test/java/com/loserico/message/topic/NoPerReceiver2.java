package com.loserico.message.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class NoPerReceiver2 {
	public static void main(String[] args) throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory(
				"tcp://192.168.1.3:61616");
		
		for(int i=0;i<2;i++){
			Thread t = new MyT(cf);
			t.start();
		}
		
	}

}

class MyT extends Thread{
	private ConnectionFactory cf = null;
	public MyT(ConnectionFactory cf){
		this.cf = cf;
	}
	
	public void run(){
		try{
			Connection connection = cf.createConnection();
			connection.start();
			
			final Session session = connection.createSession(Boolean.TRUE,
					Session.AUTO_ACKNOWLEDGE);
			
			Destination destination = session.createTopic("MyTopic");
			
			for(int i=0;i<1;i++){
				final MessageConsumer consumer = session.createConsumer(destination);
				consumer.setMessageListener(new MessageListener() {
					public void onMessage(Message message) {
						TextMessage txtMsg = (TextMessage)message; 
						try{
					        System.out.println(consumer+"收到消 息：" + txtMsg.getText());
					        session.commit();
						}catch(Exception err){
							err.printStackTrace();
						}
					}
				});
			}
		}catch(Exception err){
			err.printStackTrace();
		}
	}
}