package com.loserico.message;

import static javax.jms.DeliveryMode.NON_PERSISTENT;
import static javax.jms.DeliveryMode.PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loser.serializer.Serializer;
import org.loser.serializer.kryo.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicTest {

	private static final Logger logger = LoggerFactory.getLogger(JmsTest.class);

	private static final String url = "tcp://192.168.1.3:61616";

	private static Serializer serializer;

	@BeforeClass
	public static void setup() {
		serializer = new KryoSerializer();
	}

	/*
	 * @of
	 * Pub/Sub的一些特点： 
	 * 1：消息订阅分为非持久订阅和持久订阅 
	 * 	非持久订阅只有当客户端处于激活状态，也就是和JMS Provider保持连接状态才能收到发送到某个主题的消息，
	 * 	而当客户端处于离线状态，这个时间段发到主题的消息将会丢失，永远不会收到。 
	 * 
	 * 	持久订阅时，客户端向JMS注册一个识别自己身份的ID，当这个客户端处于离线 时，JMS Provider会为这个ID 保存所有发送到主题的消息，
	 * 	当客户再次连接到JMS Provider时，会根据自己的ID 得到所有当自己处于离线时发送到主题的消息。 
	 * 
	 * 2：如果用户在receive 方法中设定了消息选择条件，那么不符合条件的消息不会被接收
	 * 3：非持久订阅状态下，不能恢复或重新派送一个未签收的消息。只有持久订阅才能恢复或重新派送一个未签收的消息。
	 * 4：当所有的消息必须被接收，则用持久订阅。当丢失消息能够被容忍，则用非持久订阅
	 * @on
	 */
	@Test
	public void testNonPersistentSend() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("non-persist-topic");

		MessageProducer producer = session.createProducer(destination);
		for (int i = 0; i < 3; i++) {
			TextMessage textMessage = session.createTextMessage("非持久topic消息" + i);
			producer.send(textMessage, NON_PERSISTENT, 1, 0);
			System.out.println("发送非持久topic消息" + i);
		}
		session.close();
		connection.close();
	}

	@Test
	public void testNonPersistentReceive() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("non-persist-topic");

		MessageConsumer consumer = session.createConsumer(destination);
		TextMessage textMessage = (TextMessage) consumer.receive();

		while (textMessage != null) {
			System.out.println("收到消息: " + textMessage.getText());
			textMessage = (TextMessage) consumer.receive();
		}

		session.close();
		connection.close();
	}

	@Test
	public void testPersistentSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("persist-topic");

		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(PERSISTENT); //默认非持久化

		//start()要延迟到setDeliveryMode(PERSISTENT)之后
		connection.start();

		for (int i = 0; i < 3; i++) {
			TextMessage textMessage = session.createTextMessage("持久topic消息" + i);
			producer.send(textMessage, NON_PERSISTENT, 1, 0);
			System.out.println("发送持久topic消息" + i);
		}
		session.close();
		connection.close();
	}

	@Test
	public void testPersistentReceiver() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		//需要在connection上设置消费者ID，用来识别消费者
		connection.setClientID("rico");

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("persist-topic");
		//需要创建TopicSubscriber来订阅
		TopicSubscriber subscriber = session.createDurableSubscriber(topic, "Topic1");
		//要设置好过后再start这个connection
		connection.start();

		/*
		 * 一定要先运行一次，等于向消息服务中间件注册这个消费者，然后再运行客户端发送消息。
		 * 这个时候，无论消费者是否在线，都会接收到，不在线的话，瑕疵连接的时候，会把没有收过的消息都接收下来
		 */
		Message message = subscriber.receive();
		while (message != null) {
			TextMessage textMessage = (TextMessage) message;
			System.out.println("收到消息：" + textMessage.getText());
			message = subscriber.receive();
//			message = subscriber.receive(1000L);
		}

		session.close();
		connection.close();

	}
}
