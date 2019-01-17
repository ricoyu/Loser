package com.loserico.message;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多个consuemr想要均衡地接收消息的话必须是由同一个session创建的才行，否则接收消息不会均衡的。
 * 
 * @author Rico Yu
 * @since 2017-01-07 10:16
 * @version 1.0
 *
 */
public class ConsumerLoadBalanceTest {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerLoadBalanceTest.class);

	private String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";
	
	@Test
	public void testLBSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("lb-queue1");
		MessageProducer producer = session.createProducer(destination);
		
		for (int i = 0; i < 30; i++) {
			String msg = "lb message " + i;
			TextMessage message = session.createTextMessage(msg);
			System.out.println("发送: " + msg);
			producer.send(message);
			session.commit();
		}
		
		session.close();
		connection.close();
	}

	@Test
	public void testloadBalanceWithListener1() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("lb-queue1");

		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener((message) -> {
			TextMessage textMessage = (TextMessage) message;
			try {
				System.out.println("Consumer1  从 lb-queue1 收到消息: " + textMessage.getText());
				session.commit();
			} catch (JMSException e) {
				logger.error("msg", e);
			}
		});
	}
	
	@Test
	public void testloadBalanceWithListener2() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("lb-queue1");
		
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener((message) -> {
			TextMessage textMessage = (TextMessage) message;
			try {
				System.out.println("Consumer2  从 lb-queue1 收到消息: " + textMessage.getText());
				session.commit();
			} catch (JMSException e) {
				logger.error("msg", e);
			}
		});
	}
}
