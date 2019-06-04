package org.loser.cache;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.cache.interfaze.SynchronousQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedissonUtilsTest {

	@Test
	public void testDefaultRedissonClient() throws InterruptedException {
		SynchronousQueue<Integer> synchronousQueue = RedissonUtils.synchronousQueue();
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			int i = 1;
			while (true) {
				try {
					synchronousQueue.put(i++);
					log.info("往SynchronousQueue里面塞值");
					log.info("歇息4秒");
					SECONDS.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		while(true) {
			log.info("等待synchronousQueue中有新值填充进去");
			Integer i = synchronousQueue.take();
			log.info("从SynchronousQueue中取出{}", i);
		}
	}
}
