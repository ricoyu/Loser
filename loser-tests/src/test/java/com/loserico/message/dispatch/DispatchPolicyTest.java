package com.loserico.message.dispatch;

/**
 * @of
 * Message Dispatch高级特性之Dispatch Policies
 * 
 * 1. 严格顺序分发策略（Strict Order Dispatch Policy） 
 *    通常ActiveMQ会保证topic consumer以相同的顺序接收来自同一个producer的消息，但有时候也需要保证不同的topic consumer以相同的顺序接收消息，
 * 然而，由于多线程和异步处理，不同的topic consumer可能会以不同的顺序接收来自不同producer的消息。 
 * 	  Strict order dispatch policy 会保证每个topic consumer会以相同的顺序接收消息，代价是性能上的损失。以下是一个配置例子： 
 * 
 * <policyEntry topic="ORDERS.>">
 * 		<dispatchPolicy> 
 * 			<strictOrderDispatchPolicy /> 
 * 		</dispatchPolicy> 
 * </policyEntry>
 * 
 * 对于Queue的配置为：
 * <policyEntry queue=">" strictOrderDispatch="false" />
 * 
 * 2. 轮询分发策略（Round Robin Dispatch Policy） 
 * 	  ActiveMQ的prefetch缺省参数是针对处理大量消息时的高性能和高吞吐量而设置的，所以缺省的prefetch参数比较大。
 * 而且缺省的dispatch policies会尝试尽可能快的填满prefetch缓冲。 
 * 然而在有些情况下，例如只有少量的消息而且单个消息的处理时间比较长，那么在缺省的prefetch和dispatch policies下，这些少量的消息总是倾向于被分发到个别的 consumer上。
 * 这样就会因为负载的不均衡分配而导致处理时间的增加。
 * Round robin dispatch policy会尝试平均分发消息，以下是一个例子： 
 * <policyEntry topic="ORDERS.>"> 
 * 		<dispatchPolicy>
 * 			<roundRobinDispatchPolicy/> 
 * 		</dispatchPolicy> 
 * </policyEntry>
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-09 10:22
 * @version 1.0
 *
 */
public class DispatchPolicyTest {

}
