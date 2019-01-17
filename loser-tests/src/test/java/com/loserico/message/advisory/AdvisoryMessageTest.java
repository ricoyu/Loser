package com.loserico.message.advisory;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ProducerInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @of
 * Advisory Message是ActiveMQ自身的系统消息地址，可以监听该地址来获取activemq的系统信息。
 * 
 * 目前支持获取如下信息：
 * 1：consumers, producers 和connections的启动和停止 
 * 2：创建和销毁temporary destinations 
 * 3：topics 和queues的消息过期 
 * 4：brokers 发送消息给destinations，但是没有consumers 
 * 5：connections 启动和停止
 * 
 * 几点说明： 
 * 1：所有Advisory的topic，前缀是：ActiveMQ.Advisory 
 * 2：所有Advisory的消息类型是：'Advisory'，所有的Advisory都有的消息属性有：originBrokerId、 originBrokerName、originBrokerURL
 * 3：具体支持的topic和queue，请参看http://activemq.apache.org/advisory-message.html
 * 
 * 打开Advisories, 默认Advisory的功能是关闭的 
 * <destinationPolicy> 
 * 		<policyMap> 
 * 			<policyEntries>
 * 				<policyEntry topic=">" advisoryForConsumed="true" /> 
 * 			</policyEntries> 
 * 		</policyMap>
 * </destinationPolicy>
 * 
 * 关闭Advisories, 有好几种方法 
 * 1：<broker advisorySupport="false"> 
 * 2：也可在Java中写:
 * 		BrokerService broker = new BrokerService(); 
 * 		broker.setAdvisorySupport(false); 
 * 		...
 * 		broker.start(); 
 * 
 * 3：也可以在ActiveMQConnectionFactory上设置'watchTopicAdvisories' 属性
 * 		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
 * 		factory.setWatchTopicAdvisories(false); 
 * 
 * 4：也可在ConnectionURl上写： "tcp://localhost:61616?jms.watchTopicAdvisories=false"
 * 
 * 使用的方法和步骤： 
 * 1：要在配置文件里面开启Advisories 
 * 2：消息发送端没有变化 
 * 3：消息接收端：
 * 	（1）根据你要接收的信息类型，来设置不同的topic，当然也可以使用AdvisorySupport这个类来辅助创建，比如你想要得到消息生产者的信息，你可以：
 * 			Topic d=session.createTopic("ActiveMQ.Advisory.Producer.Topic.MyTopic"); 
 * 		也可以使用：
 * 			Topic d = session.createTopic("MyTopic"); 
 * 			Destination d2 = AdvisorySupport.getProducerAdvisoryTopic(destination);
 * 
 * 	（2）由于这个topic默认不是持久化的，所以应该先开启接收端，然后再发送topic信息
 * 	
 * 	（3）接收消息的时候，接收到的消息类型是ActiveMQMessage，所以类型转换的时候，要转换成ActiveMQMessage，然后再通过getDataStructure方法来得到具体的信息对象，如：
 * 			if (message instanceof ActiveMQMessage) { 
 * 				try { 
 * 					ActiveMQMessage aMsg = (ActiveMQMessage) message;
 * 					ProducerInfo prod = (ProducerInfo) aMsg.getDataStructure();
 * 					System.out.println("count==="+aMsg.getProperty("producerCount"));
 * 					System.out.println(" prodd==="+prod.getProducerId()); 
 * 				} catch (Exception e) {
 * 					e.printStackTrace(); 
 * 				} 
 * 			}
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-11 11:12
 * @version 1.0
 *
 */
public class AdvisoryMessageTest {

	private String url = "tcp://192.168.1.3:61616";
	private static final Logger logger = LoggerFactory.getLogger(AdvisoryMessageTest.class);

	@Test
	public void testAdvisotyReceiver() throws JMSException, IOException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic("ActiveMQ.Advisory.Producer.Topic.topic-advisory");
		MessageConsumer consumer = session.createConsumer(destination);
		Message message = consumer.receive();

		while (message != null) {
			ActiveMQMessage activeMQMessage = (ActiveMQMessage) message;
			ProducerInfo producerInfo = (ProducerInfo) activeMQMessage.getDataStructure();
			System.out.println("count===" + activeMQMessage.getProperty("producerCount"));
			System.out.println(" prodd===" + producerInfo.getProducerId());
			message = consumer.receive();
		}
		
		session.close();
		connection.close();
	}
}
