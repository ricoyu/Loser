package com.loserico.zookeeper;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperTest {
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperTest.class);

	ZooKeeper zookeeper;

	@Before
	public void setup() {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		try {
//			String connectString = "118.178.252.68:2181";
			String connectString = "192.168.102.103:2181";
//			String connectString = "192.168.1.164:2181";
//			String connectString = "192.168.1.3:2181, 192.168.1.4:2181, 192.168.1.6:2181";
			zookeeper = new ZooKeeper(connectString, 5000,
					(event) -> {
						logger.info("Receive watcher event: " + event);
						if (event.getState() == KeeperState.SyncConnected) {
							countDownLatch.countDown();
						}
					});
			countDownLatch.await();
			System.out.println("========= Done! ==========");
		} catch (IOException | InterruptedException e) {
			logger.error("msg", e);
		}
	}

	@Test
	public void testCreate() {
		try {
			String result = zookeeper.create("/rico", "rico".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			logger.info(result);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelete() {
		try {
			zookeeper.delete("/finance-centre/ticketno/SUB", 18);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
