package com.loserico.message.consumer;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @of
 * Consumer高级特性之Message Selectors
 * 
 * JMS Selectors用在获取消息的时候，可以基于消息属性和Xpath语法对消息进行过滤。JMS Selectors
 * 由SQL92语义定义。以下是个Selectors的例子:
 * 		consumer = session.createConsumer(destination, "JMSType = 'car' AND weight > 2500"); 
 * 
 * 1:JMS Selectors表达式中，可以使用IN、NOT IN、LIKE等
 * 2:需要注意的是，JMS Selectors表达式中的日期和时间需要使用标准的long型毫秒值 
 * 3:表达式中的属性不会自动进行类型转换，例如:
 * 		myMessage.setStringProperty("NumberOfOrders", "2"); 
 * 	   那么此时"NumberOfOrders > 1" 求值结果会是false 
 * 4:Message Groups虽然可以保证具有相同message group的消息被唯一的consumer顺序处理，但是却不能确定被哪个consumer处理。
 * 	   在某些情况下，Message Groups可以和JMS Selector一起工作，
 *	   例如:设想有三个consumers分别是A、B和C。你可以在producer中为消息设置三个message groups分别是"A"、"B"和"C"。
 *	   然后令consumer A使用"JMXGroupID = 'A'"作为selector。B和C也同理。
 *	   这样就可以保证message group A的消息只被consumer A处理。需要注意的是，这种做法有 以下缺点:
 * 		（1）producer必须知道当前正在运行的consumers，也就是说producer和consumer被耦合到一起。
 * 			意思是说Sender会知道要创建几个Group，每个Group又被某个Consumer消费，其实这问题不大。
 * 		（2）如果某个consumer失效，那么应该被这个consumer消费的消息将会一直被积压在broker上。
 * 			可以有多个Consumer有相同的Selector，这样只要这几个Consumer没有全挂，消息就会被消费。
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-13 14:05
 * @version 1.0
 *
 */
public class MessageSelectorSender {

	private static final String GROUP_ID = "JMSXGroupID";
	private static final Logger logger = LoggerFactory.getLogger(MessageGroupSender.class);

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("msg-selector-queue");

		MessageProducer producer = session.createProducer(destination);

			/*
			 * 发3条消息，他们属于一组。消息的消费者不需要配置"JMSXGroupID"，broker会将同一组消息随机选择一个Consumer发送
			 */
			for (int j = 0; j < 3; j++) {
				String text = "Message Group 消息" + j;
				TextMessage message = session.createTextMessage(text);
				message.setStringProperty(GROUP_ID, "MessageSelectorGroup");
				message.setIntProperty("age", 2);
				System.out.println("发送消息: " + text);
				producer.send(message);
		}
		session.close();
		connection.close();
	}
}
