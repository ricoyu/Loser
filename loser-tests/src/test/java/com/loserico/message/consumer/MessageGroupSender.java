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
 * Consumer高级特性之Message Groups
 * 
 * Message Groups就是对消息分组，它是Exclusive Consumer功能的增强： 
 * 		逻辑上，Message Groups 可以看成是一种并发的Exclusive Consumer。跟所有的消息都由唯一的consumer处理不同，
 * JMS消息属性JMSXGroupID 被用来区分message group。 Message Groups特性保证所有具有相同JMSXGroupID 的消息会被分发到相同的consumer
 * （只要这个consumer保持active）。
 * 
 * 为什么要Exclusive Consumer
 * 		消息1、2、3如有顺序关系，即1处理完了才能处理2、3。如果有三个Consumer分别处理这三条消息，那么这种次序是无法保证的。
 * 所以引入Exclusive Consumer，即这三条消息都交给一个Consumer去处理，这样才能保证1处理完了才开始处理2、3
 * 
 * 另外一方面，Message Groups特性也是一种负载均衡的机制。在一个消息被分发到consumer之前，broker首先检查消息JMSXGroupID属性。
 * 如果存在，那么broker 会检查是否有某个consumer拥有这个message group。如果没有，那么broker会选择一个consumer， 并将它关联到这个message group。
 * 此后，这个consumer会接收这个message group的所有消息，直到： 
 * 	1：Consumer被关闭 
 *  2：Message group被关闭，通过发送一个消息，并设置这个消息的JMSXGroupSeq为-1
 * 
 * 创建一个Message Groups，只需要在message对象上设置属性即可，如下：
 * 		message.setStringProperty("JMSXGroupID","GroupA");
 * 
 * 关闭一个Message Groups，只需要在message对象上设置属性即可，如下：
 * 		message.setStringProperty("JMSXGroupID","GroupA");
 * 		message.setIntProperty("JMSXGroupSeq", -1);
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-12 16:27
 * @version 1.0
 *
 */
public class MessageGroupSender {

	private static final String GROUP_ID = "JMSXGroupID";
	private static final Logger logger = LoggerFactory.getLogger(MessageGroupSender.class);

	private static String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	public static void main(String[] args) throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("msg-group-queue");

		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 2; i++) {
			/*
			 * 发3条消息，他们属于一组。消息的消费者不需要配置"JMSXGroupID"，broker会将同一组消息随机选择一个Consumer发送
			 */
			for (int j = 0; j < 3; j++) {
				String text = "Message Group" + i + " 消息" + j;
				TextMessage message = session.createTextMessage(text);
				message.setStringProperty(GROUP_ID, "Group" + i);
				System.out.println("发送消息: " + text);
				producer.send(message);
			}
		}
		session.close();
		connection.close();
	}
}
