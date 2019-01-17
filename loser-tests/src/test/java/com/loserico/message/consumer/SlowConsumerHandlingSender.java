package com.loserico.message.consumer;

/**
 * @of
 * Consumer高级特性之Slow Consumer Handling
 * 
 * Prefetch机制 
 * 		ActiveMQ通过Prefetch机制来提高性能，方式是在客户端的内存里可能会缓存一定数量的消息。缓存消息的数量由prefetch limit来控制。
 * 当某个consumer的prefetchbuffer已经达到上限，那么broker不会再向consumer分发消息，直到consumer向broker发 送消息的确认，确认后的消息将会从缓存中去掉。
 * 		可以通过在ActiveMQConnectionFactory或者ActiveMQConnection上设置ActiveMQPrefetchPolicy对象来配置prefetch policy。也可以通过connection options或者
 * destination options来配置。例如： 
 * 		tcp://localhost:61616?jms.prefetchPolicy.all=50
 * 		tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=1 
 * 		queue = new ActiveMQQueue("TEST.QUEUE?consumer.prefetchSize=10"); 
 * 
 * prefetch size的缺省值如下：
 * 	1：persistent queues (default value: 1000) 
 * 	2：non-persistent queues (default value: 1000) 
 * 	3：persistent topics (default value: 100) 
 * 	4：non-persistent topics (default value: Short.MAX_VALUE -1)
 * 
 * 慢Consumer处理 
 * 		慢消费者会在非持久的topics上导致问题：一旦消息积压起来，会导致broker把大量消息保存在内存中，broker也会因此而变慢。
 * 目前ActiveMQ使用Pending Message Limit Strategy来解决这个问 题。除了prefetch buffer之外，你还要配置缓存消息的上限，
 * 超过这个上限后，新消息到来时会丢弃旧消息。 通过在配置文件的destination map中配置PendingMessageLimitStrategy，可以为不用的topic namespace配置不同的策略。 
 * 
 * Pending Message Limit Strategy（等待消息限制策略）目前有以下两种： 
 * 	1： Constant Pending Message Limit Strategy
 * 		Limit可以设置0、>0、-1三种方式： 0表示：不额外的增加其预存大小。>0表示：再额外的增加其预存大小。-1表示：不增加预存也不丢弃旧的消息。
 * 		这个策略使用常量限制，配置如下： 
 * 			<constantPendingMessageLimitStrategy limit="50"/> 
 * 	
 * 	2：Prefetch Rate Pending Message Limit Strategy
 * 		这种策略是利用Consumer的之前的预存的大小乘以其倍数等于现在的预存大小。比如： 
 * 			<prefetchRatePendingMessageLimitStrategy multiplier="2.5"/> 
 * 
 * 	3：说明：在以上两种方式中，如果设置0意味着除了prefetch之外不再缓存消息；如果设置-1意味着禁止丢弃消息。
 * 
 * 配置消息的丢弃策略，目前有三种方式：
 * 	1：oldestMessageEvictionStrategy：这个策略丢弃最旧的消息。
 * 	2：oldestMessageWithLowestPriorityEvictionStrategy：这个策略丢弃最旧的，而且具有最低优先级的消息。
 * 	3：uniquePropertyMessageEvictionStrategy：从5.6开始，可以根据自定义的属性来进行抛弃，
 * 		比如<uniquePropertyMessageEvictionStrategy propertyName=“STOCK” />，这就表示抛弃属性名称为Stock的消息
 * 
 * 配置示例： 
 * 		<destinationPolicy> 
 * 			<policyMap> 
 * 				<policyEntries>
 * 					<policyEntry topic="FOO.>"> 
 * 						<dispatchPolicy> 
 * 							<roundRobinDispatchPolicy />
 * 						</dispatchPolicy> 
 * 					</policyEntry> 
 * 					<policyEntry topic="ORDERS.>"> 
 * 						<dispatchPolicy>
 * 							<strictOrderDispatchPolicy /> 
 * 						</dispatchPolicy> 
 * 					</policyEntry> 
 * 					<policyEntry topic="PRICES.>"> 
 * 						<!-- lets force old messages to be discarded for slow consumers --> 
 * 						<pendingMessageLimitStrategy> 
 * 							<constantPendingMessageLimitStrategy limit="10"/>
 * 						</pendingMessageLimitStrategy> 
 * 					</policyEntry> 
 * 					<policyEntry tempTopic="true" advisoryForConsumed="true" /> 
 * 					<policyEntry tempQueue="true" advisoryForConsumed="true" /> 
 * 				</policyEntries> 
 * 			</policyMap> 
 * 		</destinationPolicy>
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-14 10:24
 * @version 1.0
 *
 */
public class SlowConsumerHandlingSender {

}
