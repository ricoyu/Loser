package com.loserico.message.async;

/**
 * @of
 * Message Dispatch高级特性之Async Sends
 * 
 * Async Sends 
 * 
 * AciveMQ支持异步和同步发送消息，是可以配置的。
 * 通常对于快的消费者， 是直接把消息同步发送过去，
 * 但对于一个Slow Consumer，你使用同步发送消息可能出现Producer堵塞等现象，慢消费者适合使用异步发送。 
 * 
 * 配置使用
 * 1：ActiveMQ默认设置dispatcheAsync=true是最好的性能设置。如果你处理的是 Fast Consumer则使用dispatcheAsync=false 
 * 2：在Connection URI级别来配置使用Async Send 
 * 		new ActiveMQConnectionFactory("tcp://locahost:61616?jms.useAsyncSend=true");
 * 3：在ConnectionFactory级别来配置使用Async Send
 * 		((ActiveMQConnectionFactory)connectionFactory).setUseAsyncSend(true);
 * 4：在Connection级别来配置使用Async Send
 * 		((ActiveMQConnection)connection).setUseAsyncSend(true);
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-09 10:08
 * @version 1.0
 *
 */
public class AyncSendTest {

}
