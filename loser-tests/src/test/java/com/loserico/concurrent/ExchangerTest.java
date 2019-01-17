package com.loserico.concurrent;

import java.util.Objects;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangerTest {
	private static final Logger log = LoggerFactory.getLogger(ExchangerTest.class);

	private String name;
	private int age;
	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final Exchanger<String> exchanger = new Exchanger<String>();
		
		service.execute(() ->{
				try {
					String data1 = "money";
					log.info("线程" + Thread.currentThread().getName() + "正在把数据 " + data1 + " 换出去");
					Thread.sleep((long) (Math.random() * 10000));
					String data2 = (String) exchanger.exchange(data1);
					System.out.println("线程" + Thread.currentThread().getName() + "换回数据为 " + data2);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			});
		
		service.execute(new Runnable() {
			public void run() {
				try {
					String data1 = "drug";
					System.out.println("线程" + Thread.currentThread().getName() + "正在把数据 " + data1 + " 换出去");
					Thread.sleep((long) (Math.random() * 10000));
					String data2 = (String) exchanger.exchange(data1);
					System.out.println("线程" + Thread.currentThread().getName() + "换回数据为 " + data2);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		});
	}

}
