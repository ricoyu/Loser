package com.loserico.message;

import static javax.jms.DeliveryMode.PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * Destination高级特性之Visual Destinations
 * http://activemq.apache.org/virtual-destinations.html
 * 
 * @of
 * 概述 
 * 
 * 虚拟Destinations用来创建逻辑Destinations，客户端可以通过它来生产和消费消息，它会把消息映射到物理Destinations。
 * 
 * ActiveMQ支持两种方式： 
 * 1：虚拟主题（Virtual Topics）
 * 2：组合Destinations（Composite Destinations）
 * 
 * 为何使用虚拟主题
 * ActiveMQ中，topic只有在持久订阅下才是持久化的。
 * 持久订阅时，每个持久订阅 者，都相当于一个queue的客户端，它会收取所有消息。(即发5条消息到一个topic，A和B两个订阅者都将收到这5条消息)
 * 这种情况下存在两个问题：
 * 1：同一应用内consumer端负载均衡的问题：
 * 		即同一个应用上的一个持久订阅不能使用多个consumer来共同承担消息处理功能。因为每个consumer都会获取所有消息。
 * 		queue模式可以解决这个问题(队列中一条消息只能被一个Consumer消费)，但broker端又不能将消息发送到多个应用端。
 * 		所以， 既要发布订阅，又要让消费者分组，这个功能JMS规范本身是没有的。
 * 2：同一应用内consumer端failover的问题：由于只能使用单个的持久订阅者，如果这个订阅者出错，则应用就无法处理消息了，系统的健壮性不高.
 * 		为了解决这两个问题，ActiveMQ中实现了虚拟Topic的功能
 * 
 * 如何使用虚拟主题 
 * 1：对于消息发布者来说，就是一个正常的Topic，名称以VirtualTopic.开头。例如 VirtualTopic.Orders，代码示例如下：
 * 		Topic destination = session.createTopic("VirtualTopic.Orders");
 * 2：对于消息接收端来说，是个队列，不同应用里使用不同的前缀作为队列的名称，即可表明自己的身份即可实现消费端应用分组。
 * 格式为: Consumer.{XX}.VirtualTopic.{YY}
 * 例如Consumer.A.VirtualTopic.Orders	说明它是名称为A的消费端，
 * 同理Consumer.B.VirtualTopic.Orders	说明是一个名称为B的客户端。
 * 可以在同一个应用里使用多个consumer消费此queue，则可以实现上面两个功能。 又因为不同应用使用的queue名称不同（前缀不同），
 * 所以不同的应用中都可以接收到全部的消息。每个客户端相当于一个持久订阅者，而且这个客户端可以使用多个消费者共同来承担消费任务。 
 * 
 * 代码示例如下： 
 * 		Destination destination = session.createQueue("Consumer.A.VirtualTopic.Orders");
 * 
 * 3：默认虚拟主题的前缀是：VirtualTopic.>
 * 自定义消费虚拟地址默认格式：Consumer.*.VirtualTopic.>
 * 自定义消费虚拟地址可以改，比如下面的配置就把它修改了。xml配置示例如下：
 * <broker xmlns="http://activemq.apache.org/schema/core">
 * 		<destinationInterceptors>
 * 			<virtualDestinationInterceptor>
 * 				<virtualDestinations>
 * 					<virtualTopic name=">" prefix="VirtualTopicConsumers.*." selectorAware="false"/>
 * 				</virtualDestinations>
 * 			</virtualDestinationInterceptor>
 * 		</destinationInterceptors>
 * </broker>
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-08 15:17
 * @version 1.0
 *
 */
public class VirtualTopicTest {

	private static final Logger logger = LoggerFactory.getLogger(VirtualTopicTest.class);
//	private static String url = "tcp://192.168.1.3:61616";
	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	@Test
	public void testVirtualTopicSend() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		Destination virtualTopic = session.createTopic("VirtualTopic.VT1");

		MessageProducer producer = session.createProducer(virtualTopic);
		producer.setDeliveryMode(PERSISTENT);

		for (int i = 0; i < 3; i++) {
			String message = "第1次 VirtualTopic.VT1 message " + i;
			System.out.println("第1次发送: " + message);
			producer.send(session.createTextMessage(message));
		}

		session.commit();
		session.close();
		connection.close();
	}

	/*
	 * VirtualTopic receiver
	 */
	public static void main(String[] args) throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < 3; i++) {
			service.execute(() -> {
				try {
					Connection connection = connectionFactory.createConnection();
					connection.start();
					Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
					Destination destination = session.createQueue("Consumer.VT1.VirtualTopic.VT1");
					MessageConsumer consumer = session.createConsumer(destination);
					consumer.setMessageListener((message) -> {
						TextMessage textMessage = (TextMessage) message;
						try {
							System.out.println(
									"消费线程" + Thread.currentThread().getName() + " 收到消息: " + textMessage.getText());
						} catch (JMSException e) {
							logger.error("msg", e);
						}
						try {
							session.commit();
//							session.close();
//							connection.close();
						} catch (JMSException e) {
							logger.error("msg", e);
						}
					});
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			});
		}
		Thread.currentThread().join();
	}
}
