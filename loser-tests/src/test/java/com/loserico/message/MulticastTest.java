package com.loserico.message;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * @of
 * ActiveMQ的动态网络链接
 * 
 * 多播协议multicast
 * 
 * ActiveMQ使用Multicast 协议将一个Service和其他的Broker的Service连接起来。
 * IP multicast是一个被用于网络中传输数据到其它一组接收者的技术。
 * Ip multicast传统的概念称为组地址。组地址是ip地址在224.0.0.0到239.255.255.255之间的ip地址。
 * ActiveMQ broker使用multicast协议去建立服务与远程的broker的服务的网络链接。
 * 
 * 基本的格式配置 multicast://ipadaddress:port?transportOptions 
 * transportOptions如下:
 * 1:group			表示唯一的组名称，缺省值default 
 * 2:minmumWireFormatVersion	被允许的最小的wireformat版本，缺省为0
 * 3:trace			是否追踪记录日志，默认false 
 * 4:useLocalHost	表示本地机器的名称是否为localhost，默认true
 * 5:datagramSize	特定的数据大小，默认值4 * 1024 
 * 6:timeToLive		消息的生命周期，默认值-1
 * 7:loopBackMode	是否启用loopback模式，默认false 
 * 8:wireFormat		默认用wireFormat命名
 * 9:wireFormat.*:	前缀是wireFormat
 * 
 * 配置示例 
 * 1:默认配置，请注意，默认情况下是不可靠的多播，数据包可能会丢失
 * 	 multicast://default 
 * 
 * 2:特定的ip和端口
 *	 multicast://224.1.2.3:6255 
 *
 * 3:特定的ip和端口以及组名
 * 	multicast://224.1.2.3:6255?group=mygroupname
 * 
 * Activemq使用multicast协议的配置格式如下 
 * <broker xmlns="http://activemq.apache.org/schema/core" brokerName="multicast" dataDirectory="${activemq.base}/data"> 
 * 	<networkConnectors> 
 * 		<networkConnector name="default-nc" uri="multicast://default"/> 
 * 	</networkConnectors>
 * 	<transportConnectors> 
 * 		<transportConnector name="openwire" uri="tcp://localhost:61616" discoveryUri="multicast://default"/>
 * 	</transportConnectors> 
 * </broker>
 * 
 * 上面的配置说明 
 * 1:uri="multicast://default"中的default是activemq默认的ip，默认动态的寻找地址
 * 2:"discoveryUri"是指在transport中用multicast的default的地址传递 
 * 3:"uri"指动态寻找可利用的地址
 * 4:如何防止自动的寻找地址？
 * （1）名称为openwire的transport，移除discoveryUri="multicast://default"即可。
 * 		传输链接用默认的名称openwire来配置broker的tcp多点链接，这将允许其它broker能够自动发现和链接到 可用的broker中。
 * （2）名称为"default-nc"的networkConnector，注释掉或者删除即可。
 * 		ActiveMQ默认的networkConnector基于multicast协议的链接的默认名称是default-nc，而且自动的去发现其他broker。去停止这种行为，只需要注销或者删除掉default-nc网络链接。
 * （3）使brokerName的名字唯一，可以唯一识别Broker的实例，默认是localhost
 * 
 * Multicast 协议和普通的tcp协议 
 * 
 * 它们是差不多的，不同的是Multicast能够自动的发现其他broker，从而替代了使用static功能列表brokers。
 * 用multicast协议可以在网络中频繁的添加和删除ip不会有影响。 multicast协议的好处是:能够适应动态变化的地址。
 * 缺点:自动的链接地址和过度的销耗网络资源。
 * 
 * @on
 * @author Rico Yu
 * @since 2017-01-06 11:32
 * @version 1.0
 *
 */
public class MulticastTest {
	
	private String url = "multicast://default";
	
	@Test
	public void testMulticastDefault() throws JMSException {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
	}

}
