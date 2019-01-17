package com.loserico.message;

/**
 * @of
 * ActiveMQ支持很多消息属性，具体可以参见 
 * http://activemq.apache.org/activemq-message-properties.html
 * 
 * 常见的一些属性说明 
 * 1：Queue的消息默认是持久化的 
 * 2：消息的优先级默认是4 
 * 3：消息发送时设置了时间戳
 * 4：消息的过期时间默认是永不过期，过期的消息进入DLQ，可以配置DLQ及其处理策略 
 * 5：如果消息是重新发送的，将会标记出来
 * 6：JMSReplyTo标识响应消息发送到哪个Queue 
 * 7：JMSCorelationID标识此消息相关联的消息id，可以用这个标识把多个消息连接起来
 * 8：JMS同时也记录了消息重发的次数，默认是6次 
 * 9：如果有一组关联的消息需要处理，可以分组：只需要设置消息组的名字和这个消息是第几 个消息
 * 10：如果消息中一个事务环境，则TXID将被设置 
 * 11：此外ActiveMQ在服务器端额外设置了消息入列和出列的时间戳
 * 12：ActiveMQ里消息属性的值，不仅可以用基本类型，还可以用List或Map类型
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-11 09:28
 * @version 1.0
 *
 */
public class MessagePropertiesTest {

}
