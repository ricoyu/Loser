package com.loserico.message.consumer;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static javax.jms.Session.CLIENT_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Repeat;

/**
 * @of
 * Consumer高级特性之Redelivery Policy
 * 
 * ActiveMQ在接收消息的Client有以下几种操作的时候，需要重新传递消息：
 * 1：Client用了transactions，且在session中调用了rollback()
 * 2：Client用了transactions，且在调用commit()之前关闭
 * 3：Client在CLIENT_ACKNOWLEDGE的传递模式下，在session中调用了recover() 
 * 
 * 可以通过设置ActiveMQConnectionFactory和ActiveMQConnection来定制想要的再次传送策略，可用的 Redelivery属性如下：
 * 1：collisionAvoidanceFactor	设置防止冲突范围的正负百分比，只有启用useCollisionAvoidance参数时才生效。也就是在延迟时间上再加一个时间波动范围。
 * 								默认值为0.15 一般不用管他
 * 2：maximumRedeliveries		最大重传次数，达到最大重连次数后抛出异常。为-1时不限制次数，为0时表示不进行重传。默认值为6。
 * 								重传次数为6表示：第一次客户端接收后没有commit，那么broker会重新传送6次，即客户端还可以接收/不commit6次
 * 								客户端总共可以接收7次
 * 3：maximumRedeliveryDelay		最大传送延迟，只在useExponentialBackOff为true时有效（V5.5），
 * 								假设首次重连间隔为10ms，倍数为2，那么第二次重连时间间隔为20ms，第三次重连时间间隔为40ms，
 * 								当重连时间间隔大的最大重连时间间隔时，以后每次重连时间间隔都为最大重连时间间隔。默认为-1。
 * 4：initialRedeliveryDelay		初始重发延迟时间，默认1000L
 * 5：redeliveryDelay			重发延迟时间，当initialRedeliveryDelay=0时生效，默认1000L
 * 6：useCollisionAvoidance		启用防止冲突功能，默认false
 * 7：useExponentialBackOff		启用指数倍数递增的方式增加延迟时间，默认false
 * 8：backOffMultiplier			重连时间间隔递增倍数，只有值大于1和启用useExponentialBackOff参数时才生 效。默认是5
 * 
 * 在接受的Client可以如下设置
 * 		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("failover:(tcp://192.168.1.106:61679,tcp://192.168.1.106:61819)?randomize=false");
 * 		RedeliveryPolicy policy = new RedeliveryPolicy(); policy.setMaximumRedeliveries(3);
 * 		cf.setRedeliveryPolicy(policy);
 * 
 * 当消息试图被传递的次数超过配置中maximumRedeliveries属性的值时，那么，broker会认定该消息是一个死消息，并被把该消息发送到死队列中。
 * 默认，aciaveMQ中死队列被声明为“ActivemMQ.DLQ”，所有不能消费的消息都被传递到该死队列中。
 * 你可以在acivemq.xml中配置individualDeadLetterStrategy属性，示例如下： 
 * 		<policyEntry queue= "> " >
 * 			<deadLetterStrategy> 
 * 				<individualDeadLetterStrategy queuePrefix= "DLQ." useQueueForQueueMessages= "true" /> 
 * 			</deadLetterStrategy> 
 * 		</policyEntry>
 * 
 * 自动删除过期消息有时需要直接删除过期的消息而不需要发送到死队列中，可以使用属性 processExpired=false来设置，示例如下：
 * 		<policyEntry queue= "> " > 
 * 			<deadLetterStrategy> 
 * 				<sharedDeadLetterStrategy processExpired= "false" /> 
 * 			</deadLetterStrategy> 
 * 		</policyEntry>
 * 
 * 存放非持久消息到死队列中
 * 默认情况下，Activemq不会把非持久的死消息发送到死队列中。如果你想把非持久的消息发送到死队列中，需要设置属性processNonPersistent=“true”，示例如 下：
 * 		<policyEntry queue= "> " >
 * 			<deadLetterStrategy> 
 * 				<sharedDeadLetterStrategy processNonPersistent= "true" />
 * 			</deadLetterStrategy> 
 * 		</policyEntry>
 * 
 * Redelivery Policy per Destination 
 * 在V5.7之后，你可以为每一个Destination配置一个Redelivery Policy。
 * 示例如：
 * 		ActiveMQConnection connection ... // Create a connection
 * 		
 * 		RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
 * 		queuePolicy.setInitialRedeliveryDelay(0); 
 * 		queuePolicy.setRedeliveryDelay(1000);
 * 		queuePolicy.setUseExponentialBackOff(false); 
 * 		queuePolicy.setMaximumRedeliveries(2);
 * 		
 * 		RedeliveryPolicy topicPolicy = new RedeliveryPolicy();
 * 		topicPolicy.setInitialRedeliveryDelay(0); 
 * 		topicPolicy.setRedeliveryDelay(1000);
 * 		topicPolicy.setUseExponentialBackOff(false); 
 * 		topicPolicy.setMaximumRedeliveries(3);
 * 		// Receive a message with the JMS API 
 * 		RedeliveryPolicyMap map = connection.getRedeliveryPolicyMap(); 
 * 		map.put(new ActiveMQTopic(">"), topicPolicy);
 * 		map.put(new ActiveMQQueue(">"), queuePolicy);
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-13 18:42
 * @version 1.0
 *
 */
public class MessageRedeliveryPolicySender {

	private static final Logger logger = LoggerFactory.getLogger(MessageGroupSender.class);

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";
	
	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		
		//手工设置重试次数
		ActiveMQConnectionFactory activeMQConnectionFactory = (ActiveMQConnectionFactory)connectionFactory;
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(3);
		activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy);
		
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
		
		Destination destination = session.createQueue("redelivery-queue");
		MessageProducer producer = session.createProducer(destination);
		
		String msg = "重传消息1";
		TextMessage message = session.createTextMessage(msg);
		System.out.println("发送消息: " + msg);
		producer.send(message);
		
		session.commit();
		session.close();
		connection.close();
	}

}
