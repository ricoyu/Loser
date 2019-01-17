package com.loserico.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
 * Failover Protocol 可用的配置参数: 
 * 
 * 1:initialReconnectDelay	在第一次尝试重连之前等待的时间长度（毫秒），默认10
 * 2:maxReconnectDelay		最长重连的时间间隔（毫秒），默认30000
 * 3:useExponentialBackOff	重连时间间隔是否以指数形式增长，默认true 
 * 4:backOffMultiplier		递增倍数，默认2.0
 * 5:maxReconnectAttempts	默认-1|0，
 * 							自版本5.6起:		-1为默认值，代表不限重试次数；0代表从不重试（只尝试连接一次，并不重连），
 * 							5.6以前的版本:	0为默认值，代表不限重试次数所有版本:如果设置 为大于0的数，代表最大重试次数
 * 6:startupMaxReconnectAttempts	初始化时的最大重连次数。一旦连接上，将使用maxReconnectAttempts 的配置，默认0
 * 7:randomize				使用随机链接，以达到负载均衡的目的，默认true 
 * 8:backup					提前初始化一个未使用连接，以便进行快速失败转移，默认false
 * 9:timeout				设置发送操作的超时时间（毫秒），默认-1
 * 10:trackMessages			设置是否缓存[故障发生时]尚未传送完成的消息，当broker一旦重新连接成功，便将这些缓存中的消息刷新到新连接的代理中，使得消息可以在broker切换前后顺利传送，默认false
 * 11:maxCacheSize			当trackMessages启用时，缓存的最大字节，默认为128*1024bytes
 * 12:updateURIsSupported	设定是否可以动态修改broker uri（自版本5.4起），默认true
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-06 11:21
 * @version 1.0
 *
 */
public class FailoverTest {

	//		String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)?randomize=false";
	String url = "failover:(tcp://192.168.1.3:61616,tcp://192.168.1.4:61616)";

	@Test
	public void testFailoverConnection() throws JMSException, InterruptedException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("failover-queue4");
		MessageProducer producer = session.createProducer(destination);

		for (int i = 0; i < 5; i++) {
			String msg = "failover-msg2-" + i;
			TextMessage textMessage = session.createTextMessage(msg);
			System.out.println("发送消息: " + msg);
			producer.send(textMessage);
			MILLISECONDS.sleep(Math.round(Math.random() * 2000));
		}

		producer.close();
		session.close();
		connection.close();
	}
}