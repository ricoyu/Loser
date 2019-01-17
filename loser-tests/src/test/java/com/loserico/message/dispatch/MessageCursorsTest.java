package com.loserico.message.dispatch;

/**
 * @of
 * Message Dispatch高级特性之Message Cursors
 * 
 * 概述
 * ActiveMQ发送持久消息的典型处理方式是：当消息的消费者准备就绪时，消息发送系统把存储的
 * 消息按批次发送给消费者，在发送完一个批次的消息后，指针的标记位置指向下一批次待发送消息的位置，进行后续的发送操作。
 * 这是一种比较健壮和灵活的消息发送方式，但大多数情况下，消息的消费者不是一直处于这种理想的活跃状态。
 * 因此，从ActiveMQ5.0.0版本开始，消息发送系统采用一种混合型的发送模式，
 * 当消息消费者处理活跃状态时，允许消息发送系统直接把持久消息发送给消费者，当消费者处于不活跃状态下，切换使 用Cursors来处理消息的发送。
 * 
 * 当消息消费者处于活跃状态并且处理能力比较强时，被持久存储的消息直接被发送到与消费者关联的发送队列
 * 当消息已经出现积压，消费者再开始活跃；或者消费者的消费速度比消息的发送速度慢 时，消息将从Pending Cursor中提取，并发送与消费者关联的发送队列。
 * 
 * Message Cursors分成三种类型 
 * 1：Store-based 
 * 2：VM 
 * 3：File-based
 * 
 * Store-based 
 * 从activemq5.0开始，默认使用此种类型的cursor，其能够满足大多数场景的使用要求。
 * 同时支持非持久消息的处理，Store-based内嵌了File-based的模式，非持久消息直接被Non-Persistent Pending Cursor所处理
 * 
 * VM 
 * 相关的消息引用存储在内存中，当满足条件时，消息直接被发送到消费者与之相关的发送队列，处理速度非常快，
 * 但出现慢消费者或者消费者长时间处于不活跃状态的情况下，无法适应。
 * 
 * File-based 
 * 当内存设置达到设置的限制，消息被存储到磁盘中的临时文件中。
 * 
 * 配置使用 
 * 在缺省情况下，ActiveMQ会根据使用的Message Store来决定使用何种类型的Message Cursors，
 * 但是你可以根据destination来配置Message Cursors，例如： 
 * 
 * 1：对Topic subscribers 
 * <destinationPolicy>
 * 		<policyMap> 
 * 			<policyEntries> 
 * 				<policyEntry topic="org.apache.>" producerFlowControl="false" memoryLimit="1mb"> 
 * 					<dispatchPolicy>
 * 						<strictOrderDispatchPolicy/> 
 * 					</dispatchPolicy> 
 * 					<deadLetterStrategy>
 * 						<individualDeadLetterStrategy topicPrefix="Test.DLQ." /> 
 * 					</deadLetterStrategy>
 * 					<pendingSubscriberPolicy> 
 * 						<vmCursor/> 
 * 					</pendingSubscriberPolicy>
 * 					<pendingDurableSubscriberPolicy> 
 * 						<vmDurableCursor/>
 * 					</pendingDurableSubscribperPolicy> 
 * 				</policyEntry> 
 * 			</policyEntries> 
 * 		</policyMap>
 * </destinationPolicy>
 * 
 * 配置说明：
 * 
 * 有效的Subscriber类型是vmCursor和fileCursor，缺省是store based cursor。
 * 有效的持久化Subscriber的cursor types是storeDurableSubscriberCursor, vmDurableCursor 和 fileDurableSubscriberCursor，缺省是store based cursor。 
 * 
 * 2. 对于Queues的配置
 * <destinationPolicy> 
 * 		<policyMap> 
 * 			<policyEntries> 
 * 				<policyEntry queue="org.apache.>">
 * 					<deadLetterStrategy> 
 * 						<individualDeadLetterStrategy queuePrefix="Test.DLQ."/>
 * 					</deadLetterStrategy> 
 * 					<pendingQueuePolicy> 
 * 						<vmQueueCursor /> 
 * 					</pendingQueuePolicy>
 * 				</policyEntry> 
 * 			</policyEntries> 
 * 		</policyMap> 
 * </destinationPolicy>
 * 
 * 配置说明：有效的类型是storeCursor, vmQueueCursor 和fileQueueCursor
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-09 09:19
 * @version 1.0
 *
 */
public class MessageCursorsTest {

}
