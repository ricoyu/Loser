package com.loserico.message.consumer;

/**
 * JMS JMSPriority 定义了十个消息优先级值，0 是最低的优先级，9 是最高的优先级。
 * 另 外，客户端应当将0‐4 看作普通优先级，5‐9 看作加急优先级。
 * 
 * 如何定义Consumer Priority的优先级呢？ 配置如下： 
 * 		queue = new ActiveMQQueue("TEST.QUEUE?consumer.priority=10"); 
 * 		consumer = session.createConsumer(queue);
 * 
 * Consumer的Priority的划分为0~127个级别，127是最高的级别，0是最低的也是ActiveMQ默认的。
 * 这种配置可以让Broker根据Consumer的优先级来发送消息，优先发送到较高优先级的Consumer上，
 * 如果某个较高优先级的Consumer消息装载慢，则Broker会把消息发送到仅次于它优先级的Consumer上。
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-12 14:14
 * @version 1.0
 *
 */
public class ConsumerPriority {

}
