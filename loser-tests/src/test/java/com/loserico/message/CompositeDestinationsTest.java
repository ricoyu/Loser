package com.loserico.message;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * @of
 * 组合队列Composite Destinations 
 * 
 * 组合队列允许用一个虚拟的destination代表多个destinations。这样就可以通过composite destinations在一个操作中同时向多个queue发送消息。 
 * 
 * 1:客户端实现的方式 在composite destinations中,多个destination之间采用","分割。
 * 例如: 
 * 		Queue queue = new ActiveMQQueue("FOO.A, FOO.B, FOO.C"); 
 * 
 * 如果你希望使用不同类型的destination,那么需要加上前缀如queue:// 或topic://,
 * 例如: 
 * 		Queue queue = new ActiveMQQueue("FOO.A,topic://NOTIFY.FOO.A");
 * 
 * 2:在xml配置实现的方式 
 * <destinationInterceptors> 
 * 		<virtualDestinationInterceptor>
 * 			<virtualDestinations> 
 * 				<compositeQueue name="MY.QUEUE"> 
 * 					<forwardTo> 
 * 						<queue physicalName="my-queue" /> 
 * 						<queue physicalName="my-queue2" /> 
 *					</forwardTo>
 * 				</compositeQueue> 
 * 			</virtualDestinations> 
 * 		</virtualDestinationInterceptor>
 * </destinationInterceptors>
 * 
 * 3:使用filtered destinations,在xml配置实现的方式 
 * <destinationInterceptors>
 * 		<virtualDestinationInterceptor> 
 * 			<virtualDestinations> 
 * 				<compositeQueue name="MY.QUEUE"> 
 * 					<forwardTo> 
 * 						<filteredDestination selector="odd = 'yes'" queue="FOO"/> 
 * 						<filteredDestination selector="i = 5" topic="BAR"/> 
 * 					</forwardTo>
 * 				</compositeQueue> 
 *			</virtualDestinations> 
 *		</virtualDestinationInterceptor>
 * </destinationInterceptors> 
 * 
 * 4:避免在network连接broker中,出现重复消息 
 * <networkConnectors>
 * 		<networkConnector uri="static://(tcp://localhost:61617)"> 
 * 			<excludedDestinations>
 * 				<queue physicalName="Consumer.*.VirtualTopic.>"/> 
 * 			</excludedDestinations>
 * 		</networkConnector> 
 * </networkConnectors>
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-08 12:01
 * @version 1.0
 *
 */
public class CompositeDestinationsTest {

	private String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)?randomize=fasle";

	@Test
	public void testSend2MultiQueue() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination multiDestination = session.createQueue("multi-queue1, multi-queue2");
		
		MessageProducer producer = session.createProducer(multiDestination);
		for (int i = 0; i < 3; i++) {
			String message = "multi-queue message" + i;
			TextMessage textMessage = session.createTextMessage(message);
			System.out.println("发送multi-queue消息: " + message);
			producer.send(textMessage);
		}
		
		session.close();
		connection.close();
	}
	
	@Test
	public void testSend2VirtualDestination() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination multiDestination = session.createQueue("multi-queue");
		
		MessageProducer producer = session.createProducer(multiDestination);
		for (int i = 0; i < 3; i++) {
			String message = "multi-queue message" + i;
			TextMessage textMessage = session.createTextMessage(message);
			System.out.println("发送multi-queue消息: " + message);
			producer.send(textMessage);
		}
		
		session.close();
		connection.close();
	}

}
