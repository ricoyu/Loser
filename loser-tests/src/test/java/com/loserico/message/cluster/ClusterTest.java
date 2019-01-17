package com.loserico.message.cluster;

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

/**
 * @of
 * Queue consumer clusters 
 * 
 * ActiveMQ支持Consumer对消息高可靠性的负载平衡消费，如果一个Consumer死掉，该消息会转发到其它的Consumer上。
 * 如果一个Consumer获得消息比其它Consumer快，那么他将获得更多的消息。
 * 因此推荐ActiveMQ的Broker和Client使用 failover：//transport的方式来配置链接。
 * 
 * Broker clusters 
 * 
 * 大部情况下是使用一系列的Broker和Client链接到一起。如果一个Broker死掉了，Client可以自动链接到其它Broker上。
 * 实现以上行为需要用failover协议作为Client。 
 * 如果启动了多个Broker，Client可以使用static discover或者Dynamic discovery 容易的从一个broker到另一个broker直接链接。
 * 这样当一个broker上没有Consumer的话，那么它的消息不会被消费的，然而该 broker会通过存储和转发的策略来把该消息发到其它broker上。
 * 特别注意：
 * 		ActiveMQ默认的两个broker，static链接后是单方向的，broker-A可以访问消费broker-B的消息，
 * 		如果要支持双向通信，需要在netWorkConnector配置的时候， 设置duplex=true 就可以了。
 * 
 * Master Slave 
 * 
 * 在5.9的版本里面，废除了Pure Master Slave的方式，
 * 目前支持： 1：Shared File System Master Slave：基于共享储存的Master-Slave：
 * 				多个broker实例使用 一个存储文件，谁拿到文件锁就是master，其他处于待启动状态，如果master挂掉了，某 个抢到文件锁的slave变成master 
 * 			2：JDBC Master Slave：基于JDBC的Master-Slave：使用同一个数据库，拿到LOCK表的写锁的 broker成为master 
 * 			3：Replicated LevelDB Store：基于ZooKeeper复制LevelDB存储的Master-Slave机制，这个 是5.9新加的
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-07 09:42
 * @version 1.0
 *
 */
public class ClusterTest {

	@Test
	public void testCluster1Send() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("cluster1");
		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 50; i++) {
			System.out.println("Send message: " + "cluster1-message-1.3--" + i);
			TextMessage message = session.createTextMessage("cluster1-message-1.3--" + i);
			producer.send(message);
		}
	}

	/**
	 * Broker3和4组成呢个双向链接，发消息到broker3，然后broker3停掉，这时候从broker4上是无效消费broker3上的消息的
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testCluster1ReceiveFrom4() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.4:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("cluster1");
		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 50; i++) {
			TextMessage message = (TextMessage) consumer.receive();
			System.out.println("Receive message from4 : " + message.getText());
		}

	}

	@Test
	public void testCluster1ReceiveFrom3() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.3:61616");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("cluster1");
		MessageConsumer consumer = session.createConsumer(destination);

		for (int i = 0; i < 50; i++) {
			TextMessage message = (TextMessage) consumer.receive();
			System.out.println("Receive message from3: " + message.getText());
		}

	}
}
