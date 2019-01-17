package com.loserico.message;

/**
 * @of
 * 什么时候使用ActiveMQ
 * 	异步调用
 * 	一对多通信
 * 	做多个系统的集成，同构、异构
 * 	作为RPC的替代
 * 	多个应用相互解耦 
 * 	作为事件驱动架构的幕后支撑
 * 	为了提高系统的可伸缩性
 * 
 * ActiveMQ优化
 * 
 * ActiveMQ的性能依赖于很多因素，比如： 
 * 1：网络拓扑结构，比如：嵌入、主从复制、网络连接 
 * 2：transport协议
 * 3：service的质量，比如topic还是queue，是否持久化，是否需要重新投递，消息超时等 
 * 4：硬件、网络、JVM和操作系统等 
 * 5：生产者的数量，消费者的数量
 * 6：消息分发要经过的destination数量，以及消息的大小等
 * 
 * 非持久化消息比持久化消息更快，原因如下：
 * 1：非持久化发送消息是异步的，Producer不需要等待Consumer的receipt消息 
 * 2：而持久化是要把消息先存储起来，然后再传递
 * 
 * 尽量使用异步投递消息，示例如： 
 * 		cf.setUseAsyncSend(true); 
 * 
 * Transaction比Non-transaction更快
 * 可以考虑内嵌启动broker，这样应用和Broker之间可以使用VM协议通讯，速度快 
 * 尽量使用基于文件的消息存储方案，比如使用KahaDB的方式
 * 调整Prefetch Limit，ActiveMQ默认的prefetch大小不同的： 
 * 1：Queue Consumer 				默认1000 
 * 2：Queue Browser Consumer			默认500 
 * 3：Persistent Topic Consumer		默认1000 
 * 4：Non-persistent Topic Consumer	默认32767 
 * 
 * Prefecth policy设置示例如下： 
 * 		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(); 
 * 		Properties props = new Properties();
 * 		props.setProperty("prefetchPolicy.queuePrefetch", "1000");
 * 		props.setProperty("prefetchPolicy.queueBrowserPrefetch", "500");
 * 		props.setProperty("prefetchPolicy.durableTopicPrefetch", "1000");
 * 		props.setProperty("prefetchPolicy.topicPrefetch", "32767");
 * 		cf.setProperties(props); 
 * 
 * 也可以在创建Destination的时候设置prefetch size，示例如下： 
 * 		Queue queue =  ActiveMQQueue("TEST.QUEUE?consumer.prefetchSize=10"); 
 * 		MessageConsumer consumer = session.createConsumer(queue); 
 * 
 * 可以考虑生产者流量控制，可以通过xml配置，代码开启方式如下：
 * 		cf.setProducerWindowSize(1024000); 
 * 
 * 可以考虑关闭消息的复制功能，也能部分提高心能，在连接工厂上设置，如下：
 * 		ActiveMQConnectionFaction cf = ….. 
 * 		cf.setCopyMessageOnSend(false); 
 * 
 * 调整TCP协议
 * 		TCP协议是ActiveMQ中最常使用的协议，常见有如下配置会影响协议性能： 
 * 	1：socketBufferSize：	socket的缓存大小，默认是65536
 * 	2：tcpNoDelay：			默认是false 
 * 		示例如： 
 * 		String url = "failover://(tcp://localhost:61616?tcpNoDelay=true)"; 
 * 		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(url); 
 * 
 * 消息投递和消息确认 
 * 		官方建议使用自动确认的模式，同时还可以开启优化确认的选项，如下：
 * 		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
 * 		cf.setOptimizeAcknowledge(true);
 * 
 * 在消费者这边，session会在一个单独的线程中分发消息给消费者，如果你使用的自动确认模式，为了增加吞吐量，你可以直接通过session传递消息给消费者，示例如下： 
 * 		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(); 
 * 		cf.setAlwaysSessionAsync(false);
 * 
 * 如果使用KahaDB进行消息存储的话，可以调整如下选项来优化性能：
 * 	1：indexCacheSize：		默认为10000，用来设定缓存页的个数，默认情况一页是4KB，一般来说缓存的 大小尽可能的设置大一些，以避免内存不足时频繁的交换。
 * 	2：indexWriteBatchSize：	默认1000，用来设置脏索引（脏索引就是cache中的index和消息存储中的index状态不一样）达到多少之后，就需要把索引存储起来。
 * 							如果你想最大化broker的速度，那么就把这个值设置的尽可能的大一些，这样的话，仅会在到达checkpoint的时候，索引才会被存储起来。
 * 							但是这样会增大系统出错的时候，丢失大量的元数据的风险。
 * 3：journalMaxFileLength：	缺省32mb，当broker的吞吐量特别大的时候，日志文件会很快被写满，这样会因为频繁的关闭文件，打开文件而导致性能低下。
 * 							你可以通过调整文件的size，减少文件 切换的频率，从而获得轻微的性能改善。
 * 4：enableJournalDiskSyncs：	缺省为true，通常，broker会在给producer确认之前，把消息同步到磁盘上（并且确保消息物化到磁盘上）。
 * 								你可以通过设置这个选项为false，从而获得本质的性 能改善。但是这样的话，多少会降低broker的可靠性。
 * 
 * @on
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-01-14 11:16
 * @version 1.0
 *
 */
public class ActiveMQOptimize {

}
