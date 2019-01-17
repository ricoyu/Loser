package org.loser.cache;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.concurrent.Semaphore;

public class SemaphoreTest1 {

	public static void main(String[] args) {
		Semaphore semaphore = RedissonUtils.semaphore("test-semaphore");
		try {
			System.out.println("第一个客户端尝试获取信号量");
			semaphore.acquire();
			System.out.println("第一个客户端获取信号量成功");
//			semaphore.acquire();
//			System.out.println("第一个客户端再次获取信号量成功");
//			SECONDS.sleep(12);
			semaphore.release();
			System.out.println("第一个客户端释放信号量成功");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
