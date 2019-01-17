package com.loserico.message;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ActiveMQ结合Spring开发最佳实践和建议 
 * 
 * 1：Camel框架支持大量的企业集成模式，可以大大简化集成组件间的大量服务和复杂的消息流。而Spring框架更注重简单性，仅仅支持基本的最佳实践。 
 * 2：Spring消息发送的核心架构是JmsTemplate，隔离了像打开、关闭Session和Producer的繁琐操作，因此应用开发人员仅仅需要关注实际的业务逻辑。
 * 		但是JmsTemplate损害了ActiveMQ的PooledConnectionFactory对session和消息 producer的缓存机制而带来的性能提升。
 * 3：新的Spring里面，可以设置 org.springframework.jms.connection.CachingConnectionFactory的sessionCacheSize ，或者干脆使用ActiveMQ的PooledConnectionFactory
 * 4：不建议使用JmsTemplate的receive()调用，因为在JmsTemplate上的所有调用都是同步的，这意味着调用线程需要被阻塞，直到方法返回，这对性能影响很大
 * 5：请使用DefaultMessageListenerContainer，它允许异步接收消息并缓存session和消息consumer，而且还可以根据消息数量动态的增加或缩减监听器的数量
 * 
 * @author Rico Yu
 * @since 2016-12-23 09:43
 * @version 1.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jms-topic-config.xml")
public class SpringJmsTopicTest {

	private static final Logger logger = LoggerFactory.getLogger(SpringJmsTopicTest.class);

	@Resource
	private JmsTemplate jmsTemplate;

	@Test
	public void testSpringTopicSend() {
		/*jmsTemplate.send((session) -> {
			logger.info("spring-topic-msg...");
			return session.createTextMessage("spring-topic-msg...");
		});*/
	}
}
