package com.loserico.message.consumer;

/**
 * 独有消费者（Exclusive Consumer） 
 * 
 * Queue中的消息是按照顺序被分发到consumers的。然而，当你有多个consumers同时从相同的queue中提取消息时，你将失去这个保证。
 * 因为这些消息是被多个线程并发的处理。有的时候，保证消息按照顺序处理是很重要的(即消息1,2,3之间业务上是有依赖关系的，要处理完消息1之后才能处理消息2)。
 * 
 * 例如，你可能不希望在插入订单操作结束之前执行更新这个订单的操作。 
 * ActiveMQ从4.x版本起开始支持Exclusive Consumer。Broker会从多个consumers中挑选一个consumer来处理queue中所有的消息，从而保证了消息的有序处理。
 * 如果这个consumer失效，那么broker会自动切换到其它的consumer。
 * 可以通过Destination Options 来创建一个Exclusive Consumer，如下： 
 * 		queue = new ActiveMQQueue("TEST.QUEUE?consumer.exclusive=true");
 * 		consumer = session.createConsumer(queue); 
 * 
 * 还可以给consumer设置优先级，以便针对网络情况进行优化，如下： 
 * 		queue = new ActiveMQQueue("TEST.QUEUE?consumer.exclusive=true&consumer.priority=10");
 * 
 * @author Rico Yu
 * @since 2017-01-11 19:36
 * @version 1.0
 *
 */
public class ExclusiveConsumer {

}
