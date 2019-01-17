package com.loserico.hystrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import rx.Observable;

/**
 * 测试注释
 * <p>
 * Copyright: Copyright (c) 2018-11-29 16:47
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class CommandHelloWorld extends HystrixCommand<String>{
	
	private String name;
	
	public CommandHelloWorld(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("Examplegroup"));
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
        // a real example would do work like a network call here
		return "Hello " + name;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String s = new CommandHelloWorld("俞雪华").execute();
		Future<String> future = new CommandHelloWorld("rico").queue();
		Observable<String> observable = new CommandHelloWorld("yu").observe();
		System.out.println(s);
		System.out.println(future.get());
		Thread.currentThread().join();
	}
}
