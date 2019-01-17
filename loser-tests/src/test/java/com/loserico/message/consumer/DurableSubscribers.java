package com.loserico.message.consumer;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @of
 * Consumer高级特性之Manage Durable Subscribers
 * 
 * 消息的持久化，保证了消费者离线后，再次进入系统，不会错过消息，但是这也会消耗很 多的资源。从5.6开始，可以对持久化消息进行如下管理： 
 * 
 * Removing inactive subscribers 
 * 我们还可能希望删除那些不活动的订阅者，如下： 
 * 		<broker name="localhost" offlineDurableSubscriberTimeout="86400000" offlineDurableSubscriberTaskSchedule="3600000"> 
 * 
 * 	1：offlineDurableSubscriberTimeout	离线多长时间就过期删除，缺省是-1，就是不删除 
 * 	2：offlineDurableSubscriberTaskSchedule	多长时间检查一次，缺省300000，单位毫秒
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-12 14:19
 * @version 1.0
 *
 */
public class DurableSubscribers {

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	private static final Logger logger = LoggerFactory.getLogger(DurableSubscribers.class);

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.setClientID("durable-sub-client1");
		
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);

		Topic destination = session.createTopic("durable-subscriber-topic");
		MessageConsumer consumer = session.createDurableSubscriber(destination, "durable-subscriber1");

		connection.start();

		consumer.setMessageListener((message) -> {
			TextMessage txtMsg = (TextMessage) message;
			try {
				System.out.println("收到消 息：" + txtMsg.getText());
			} catch (JMSException e) {
				logger.error("msg", e);
				try {
					session.close();
					connection.close();
				} catch (JMSException e1) {
					logger.error("msg", e1);
				}
			}
		});
	}
	
	@Test
	public void testSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		
		Destination destination = session.createTopic("durable-subscriber-topic");
		MessageProducer producer = session.createProducer(destination);
		connection.start();
		
		String msg = "hello durable subscriber";
		TextMessage message = session.createTextMessage(msg);
		producer.send(message);
		
		session.close();
		connection.close();
		
		
	}
}
