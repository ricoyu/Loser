package com.loserico.message.staticnetwork;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class QueueReceiver1 {
	public static void main(String[] args) throws Exception {
		ConnectionFactory cf = new ActiveMQConnectionFactory(
//				"tcp://localhost:61616");
				"tcp://192.168.1.3:61616");
		
		for(int i=0;i<3;i++){
			Thread t = new MyThread(cf);
			t.start();
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
class MyThread extends Thread{
	private ConnectionFactory cf = null;
	public MyThread(ConnectionFactory cf){
		this.cf = cf;
	}
	public void run(){
		try{
			final Connection connection = cf.createConnection();
			connection.start();
	
			final Session session = connection.createSession(Boolean.TRUE,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("VirtualTopicConsumers.A.VirtualTopic.MyTopic3");//"my-queue");
	
			MessageConsumer consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {
				
				public void onMessage(Message msg) {
//					if(!(msg instanceof TextMessage)){
//						try {
//							session.commit();
//						} catch (JMSException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					TextMessage txtMsg = (TextMessage)msg;
					try {
						System.out.println("Receiver11111===="+txtMsg.getText());
					} catch (JMSException e1) {
						e1.printStackTrace();
					}
					try {
						session.commit();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						session.close();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						connection.close();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			
		}catch(Exception err){
			err.printStackTrace();
		}
	}
}