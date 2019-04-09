package com.loserico.zookeeper;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatcherTest {

	private ZookeeperClient zookeeperClient;

	@Before
	public void setup() {
		zookeeperClient = ZookeeperClient
				.initialize("192.168.102.104:2181,192.168.102.106:2181,192.168.102.107:2181/loser-zk/watcher", 4000);
	}

	@Test
	public void testGetData() {
		zookeeperClient.createEphemeral("/myname");
		String value = zookeeperClient.getStr("/myname", (event) -> {
			log.info(toJson(event));
			log.info("Path:{}", event.getPath());
			log.info("State:{}", event.getState());
			log.info("Type:{}", event.getType());
			log.info("new value:{}", zookeeperClient.getStr("/myname", true));
		});
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
