package com.loserico.message;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.jms.DeliveryMode.PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static javax.jms.Session.CLIENT_ACKNOWLEDGE;
import static javax.jms.Session.SESSION_TRANSACTED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.jms.BytesMessage;
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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loser.serializer.Serializer;
import org.loser.serializer.kryo.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @of
 * JMS 消息由以下几部分组成：消息头，属性和消息体
 * 
 * 消息头包含消息的识别信息和路由信息，消息头包含一些标准的属性如下： 
 * 1：JMSDestination：消息发送的目的地：主要是指Queue和Topic，自动分配
 * 
 * 2：JMSDeliveryMode：传送模式。
 * 		有两种：持久模式和非持久模式。
 * 		一条持久性的 消息应该被传送“一次仅仅一次”，这就意味者如果JMS提供者出现故障，该消息并不会丢失，它会在服务器恢复之后再次传递。
 * 		一条非持久的消息最多会传送 一次，这意味这服务器出现故障，该消息将永远丢失。
 * 
 * 3：JMSExpiration：消息过期时间，等于Destination 的send 方法中的timeToLive值加上发送时刻的GMT 时间值。
 * 		如果timeToLive 值等于零，则JMSExpiration 被设为零，表示该消息永不过期。如果发送后，在消息过期时间之后消息还没有被发送到目的地，则该消息被清除
 * 
 * 4：JMSPriority：消息优先级，从0-9 十个级别，0-4 是普通消息，5-9 是加急消息。
 * 		JMS 不要求JMS Provider 严格按照这十个优先级发送消息，但必须保证加急消息要先于普通消息到达。
 * 		默认是4级。
 * 
 * 5：JMSMessageID：唯一识别每个消息的标识，由JMS Provider 产生。
 * 
 * 6：JMSTimestamp：一个JMS Provider在调用send()方法时自动设置的。它是消息被发送和消费者实际接收的时间差。
 * 
 * 7：JMSCorrelationID：用来连接到另外一个消息，典型的应用是在回复消息中连接到原消息。
 * 		在大多数情况下，JMSCorrelationID用于将一条消息标记为对JMSMessageID标示的上一条消息的应答，
 * 		不过，JMSCorrelationID可以是任何值，不仅仅是JMSMessageID。由开发者设置
 * 
 * 8：JMSReplyTo：提供本消息回复消息的目的地址。由开发者设置
 * 9：JMSType：消息类型的识别符。由开发者设置
 * 
 * 10：JMSRedelivered：如果一个客户端收到一个设置了JMSRedelivered属性的消息，则表示可能客户端曾经在早些时候收到过该消息，但并没有签收(acknowledged)。
 * 		如果该消息被重新传送，JMSRedelivered=true反之，JMSRedelivered =false。
 * 
 * 消息体，JMS API定义了5种消息体格式，也叫消息类型，可以使用不同形式发送接收数据，并可以兼容现有的消息格式。
 * 包括：TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage
 * 
 * 消息属性，包含以下三种类型的属性：
 * 1：应用程序设置和添加的属性，比如：Message.setStringProperty(“username”,username);
 * 2：JMS定义的属性使用“JMSX”作为属性名的前缀，connection.getMetaData().getJMSXPropertyNames()， 方法返回所有连接支持的JMSX 属性的名字。
 * 3：JMS供应商特定的属性
 * 
 * JMS定义的属性如下：
 * 1：JMSXUserID：发送消息的用户标识，发送时提供商设置
 * 2：JMSXAppID：发送消息的应用标识，发送时提供商设置
 * 3：JMSXDeliveryCount：转发消息重试次数,第一次是1，第二次是2，… ，发送时提供商设置
 * 4：JMSXGroupID：消息所在消息组的标识，由客户端设置
 * 5：JMSXGroupSeq：组内消息的序号第一个消息是1，第二个是2，…，由客户端设置
 * 6：JMSXProducerTXID ：产生消息的事务的事务标识，发送时提供商设置
 * 7：JMSXConsumerTXID ：消费消息的事务的事务标识，接收时提供商设置
 * 8：JMSXRcvTimestamp ：JMS 转发消息到消费者的时间，接收时提供商设置
 * 9：JMSXState：假定存在一个消息仓库，它存储了每个消息的单独拷贝，且这些消息从原始消息被发送时开始。
 * 		每个拷贝的状态有：1（等待），2（准备），3（到期）或4（保留）。
 * 		由于状态与生产者和消费者无关，所以它不是由它们来提供。它只和在仓库中查找消息相关，因此JMS没有提供这种API。由提供商设置
 * 
 * @on
 */
public class JmsTest {

	private static final Logger logger = LoggerFactory.getLogger(JmsTest.class);

	//	private static final String url = "tcp://localhost:61616";
//	private static final String url = "tcp://192.168.1.3:61616";
	private static final String url = "tcp://192.168.2.103:61616";
//	private static final String url = "udp://192.168.1.3:61619";
//	private static final String url = "ssl://192.168.1.3:61619";
//	private static final String url = "nio://192.168.1.3:61618";

	private static Serializer serializer;

	@BeforeClass
	public static void setup() {
		serializer = new KryoSerializer();
	}

	/*
	 * @of
	 * PTP的一些特点： 
	 * 1：如果在Session 关闭时，有一些消息已经被收到，但还没有被签收(acknowledged)，那么，当消费者下次连接到相同的队列时，这些消息还会被再次接收 
	 * 2：如果用户在receive 方法中设定了消息选择条件，那么不符合条件的消息会留在队列中，不会被接收到 
	 * 3：队列可以长久地保存消息直到消费者收到消息。消费者不需要因为担心消息会丢失而时刻和队列保持激活的连接状态，充分体现了异步传输模式的优势
	 * @on
	 */
	@Test
	public void testJmsSend() throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://myBroker?marshal=false&broker.persistent=true");
//		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.4:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		/*
		 * Session.CLIENT_ACKNOWLEDGE：客户通过调用消息的acknowledge方法确认消息。
		 * 需要注意的是，在这种模式中，确认是在会话层上进行，确认一个被消费的消息 将自动确认所有已被会话消费的消息。 例如，如果一个消息消费者消费了10
		 * 个消息，然后确认第5 个消息，那么所有10 个消息都被确认。
		 */
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		Destination destination = session.createQueue("queue-loser1");

		MessageProducer producer = session.createProducer(destination);
		for (int i = 0; i < 3; i++) {
			TextMessage message = session.createTextMessage("发送Message--" + i +" 到192.168.1.4");
			SECONDS.sleep(1);
			producer.send(message, PERSISTENT, 1, 100000);
			System.out.println("已发送Broker4： " + "Message--" + i);
		}

		session.close();
		connection.close();
	}

	@Test
	public void testJmsReceiver() throws JMSException {
//		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://myBroker?marshal=false&broker.persistent=true");
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.2.103:61616");
//		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("hx:user:create");

		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 3; i++) {
			Object object = consumer.receive();
			TextMessage message = (TextMessage) consumer.receive();
			session.commit();
			System.out.println("从broker3收到消息: " + message.getText());
		}

		session.close();
		connection.close();
	}
	
	@Test
	public void testJmsReceiver2() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
//		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("queue-loser1");
		
		MessageConsumer consumer = session.createConsumer(destination);
		
		for (int i = 0; i < 3; i++) {
			TextMessage message = (TextMessage) consumer.receive();
			session.commit();
			System.out.println("收到消息broker3的消息: " + message.getText());
		}
		
		session.close();
		connection.close();
	}

	@Test
	public void testJmsReceiverHandler() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("queue-loser1");
		MessageConsumer consumer = session.createConsumer(destination);

		consumer.setMessageListener((message) -> {
			try {
				logger.info(((TextMessage) message).getText());
			} catch (JMSException e) {
				logger.error("msg", e);
			}
		});
	}

	@Test
	public void testSendTransacted() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("Test.Transactions");
		MessageProducer producer = session.createProducer(topic);
		int count = 0;
		for (int i = 0; i < 1000; i++) {
			Message message = session.createTextMessage("message" + i);
			producer.send(message);

			if (i != 0 && i % 10 == 0) {
				session.commit();
			}
		}
	}

	@Test
	public void testSendTransactedQueue() {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		try {
			Connection connection = connectionFactory.createConnection();
			connection.start();

			/*
			 * Session.AUTO_ACKNOWLEDGE：当客户成功的从receive方法返回的时候，或者从
			 * MessageListener.onMessage方法成功返回的时候，会话自动确认客户收到的消息。
			 */
			Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("transacted-queue");

			MessageProducer producer = session.createProducer(destination);
			for (int i = 0; i < 3; i++) {
				TextMessage message = session.createTextMessage("Message" + i);
				MILLISECONDS.sleep(Math.round(Math.random() * 3000));
				producer.send(message, PERSISTENT, 1, 30000);
				//				producer.send(message);
				System.out.println("Sent: Message" + i);
			}

			session.commit();
			session.close();
			connection.close();
		} catch (JMSException | InterruptedException e) {
			logger.error("msg", e);
		}
	}

	@Test
	public void testReceiveTransactedQueue() {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("transacted-queue");

			MessageConsumer consumer = session.createConsumer(destination);
			for (int i = 0; i < 3; i++) {
				TextMessage message = (TextMessage) consumer.receive();
				session.commit();
				System.out.println(message.getText());
			}
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRandom() {
		List<Long> randoms = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			//			randoms.add(Math.round(Math.random() * 3000));
			randoms.add((long) (Math.random() * 3000));
		}
		randoms.stream().sorted().forEach(System.out::println);
	}

	@Test
	public void testRandom2() {
		List<Long> randoms = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			randoms.add(ThreadLocalRandom.current().nextLong(3000));
		}
		randoms.stream().sorted().forEach(System.out::println);
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
	 * 
	 * Durable / Non-durable subscriptions[for Topics]
	 * Subscription refers to subscription on a topic. With a durable subscription, 
	 * if the subscriber [which has subscribed for message on a topic] is down for some time, 
	 * once it comes up, it will receive all the messages sent for it(including the ones sent when it was down). 
	 * With Non-durable subscription, a subscriber will receive only the messages when it was connected to topic 
	 * (will loose all the ones sent when it was down). 
	 * 
	 * Note that this is not applicable for Queue’s as they can be considered always durable 
	 * [only one consumer, and it will always receive the message destined for it in queue].
	 * @on
	 */
	@Test
	public void testTopicSend() throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("午饭");

		MessageProducer producer = session.createProducer(topic);
		BytesMessage message = new ActiveMQBytesMessage();
		message.writeBytes(serializer.toBytes(LocalDateTime.now()));

		MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(3000));
		producer.send(message, PERSISTENT, 1, 0);
		System.out.println("Sent message: " + message.toString());
		session.commit();
	}

	@Test
	public void testTopicReceive() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic("午饭");

		MessageConsumer consumer = session.createConsumer(topic);
		BytesMessage message = (BytesMessage) consumer.receive();
		while (message != null) {
			byte[] bytes = new byte[(int) message.getBodyLength()];
			message.readBytes(bytes);
			LocalDateTime localDateTime = serializer.toObject(bytes, LocalDateTime.class);
			System.out.println(localDateTime.format(ofPattern("yyyy-MM-dd HH:mm:ss")));
			message = (BytesMessage) consumer.receive();
			session.commit();
		}
	}

	/*
	 * 测试消息确认
	 */
	@Test
	public void testJmsTransactedAckSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, SESSION_TRANSACTED);
		Destination destination = session.createQueue("transacted-ack");
		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 5; i++) {
			String message = "transacted-ack-message" + i;
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
			System.out.println("Sent message: " + message);
		}
		session.commit();
		session.close();
		connection.close();
	}

	@Test
	public void testJmsTransactedAckReceiver() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, SESSION_TRANSACTED);
		Destination destination = session.createQueue("transacted-ack");
		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 5; i++) {
			TextMessage textMessage = (TextMessage) consumer.receive(1000);
			System.out.println(textMessage.getText());
		}
		//事务性消息调用commit()后自动确认
		session.commit();
	}

	/*
	 * 非事务性session，如果为AUTO_ACKNOWLEDGE，调用从receive()方法成功返回后自动确认
	 */
	@Test
	public void testNonTransactedAckAutoSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("non-transacted-ack-auto");
		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 5; i++) {
			String message = "non-transacted-ack-auto-msg" + i;
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
			System.out.println("发送消息: " + message);
		}
	}

	@Test
	public void testNonTransactedAckAutoReceiver() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("non-transacted-ack-auto");
		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 5; i++) {
			TextMessage textMessage = (TextMessage) consumer.receive();
			System.out.println("收到消息：" + textMessage.getText());
		}
	}

	@Test
	public void testNonTransactedAckClientSender() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
		Destination destination = session.createQueue("non-transacted-ack-client");
		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 5; i++) {
			String message = "non-transacted-ack-client-msg" + i;
			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
			System.out.println("发送消息: " + message);
		}

		session.close();
		connection.close();
	}

	@Test
	public void testNonTransactedAckClientConsumer() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
		Destination destination = session.createQueue("non-transacted-ack-client");
		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 5; i++) {
			TextMessage textMessage = (TextMessage) consumer.receive();
			System.out.println("收到消息: " + textMessage.getText());
			if (i == 0) {
				textMessage.acknowledge();
			}
		}
		session.close();
		connection.close();
	}

	/*
	 * @of
	 * 消息持久性，JMS 支持以下两种消息提交模式： 
	 * PERSISTENT		指示JMS provider持久保存消息，以保证消息不会因为JMS provider的失败而丢失 
	 * NON_PERSISTENT	不要求JMS provider持久保存消息
	 * 
	 * Persistent/Non-persistent Messages [Aka Delivery Mode : for Messages]
	 * Delivery Mode refers to persistence/non-persistence of messages which can be specified on MessageProducer level as well as on individual message level. 
	 * Default delivery mode is PERSISTENT, means messages will be stored on disk/database until it is consumed by a consumer, and will survive a broker restart. 
	 * When using non-persistent delivery, if you kill a broker then you will lose all in-transit messages.
	 * @on
	 */
	@Test
	public void testPersistentProducer() {

	}

	/**
	 * 测试生产者提供一个回复地址，接收到收到消息后发送一个回复消息给生产者
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testTempQueueProducer() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination tempDest = session.createTemporaryQueue();
		Destination destination = session.createQueue("temp-queue-demo");

		MessageProducer producer = session.createProducer(destination);
		TextMessage textMessage = session.createTextMessage("temp-queue-demo-message");
		textMessage.setJMSReplyTo(tempDest);

		producer.send(textMessage);
		System.out.println("发送消息: temp-queue-demo-message");

		MessageConsumer consumer = session.createConsumer(tempDest);
		TextMessage replyMessage = (TextMessage) consumer.receive();
		System.out.println("收到回复消息: " + replyMessage.getText());

		session.close();
		connection.close();
	}

	@Test
	public void testTempQueueConsumer() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("temp-queue-demo");
		MessageConsumer consumer = session.createConsumer(destination);
		TextMessage message = (TextMessage) consumer.receive();
		System.out.println("Consumer收到消息: " + message.getText());

		Destination replyDest = message.getJMSReplyTo();
		MessageProducer producer = session.createProducer(replyDest);
		TextMessage replyMessage = session.createTextMessage("你发送的消息已收到,这是一条回复消息");
		producer.send(replyMessage);

		session.close();
		connection.close();
	}
}
