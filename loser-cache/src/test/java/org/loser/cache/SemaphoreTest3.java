package org.loser.cache;

import com.loserico.cache.redis.RedissonUtils;
import com.loserico.cache.redis.concurrent.Semaphore;

public class SemaphoreTest3 {

	public static void main(String[] args) {
		Semaphore semaphore = RedissonUtils.semaphore("test-semaphore");
		try {
			System.out.println("第三个客户端尝试获取信号量");
			semaphore.acquire();
			System.out.println("第三个客户端获取信号量成功，睡眠12秒");
			semaphore.release();
			System.out.println("第三个客户端释放信号量成功");
			semaphore.delete();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}