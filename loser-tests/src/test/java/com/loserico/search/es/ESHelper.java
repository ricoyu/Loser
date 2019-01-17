package com.loserico.search.es;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESHelper {

	public static final String INDEX_NAME = "mytest2";
	public static final String DOC_TYPE = "product";

	public static Client client = null;

	static {
		Settings settings = Settings.builder()
				/*
				 * 指定集群的名称
				 * 就算是单节点的elasticsearch，启动后也会生成一个集群的名字，默认为elasticsearch
				 * 
				 * 配置项为cluster.name: my-application
				 * @on
				 */
				.put("cluster.name", "sexy-uncle")
				//探测集群中机器的状态
				.put("client.transport.sniff", true).build();

		try {
			client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName("192.168.102.103"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
