package com.loserico.concurrent.basic;

import java.lang.Thread.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainThread {
	private static final Logger log = LoggerFactory.getLogger(MainThread.class);

	public static void main(String[] args) {
		long id = Thread.currentThread().getId();
		String name = Thread.currentThread().getName();
		int priority = Thread.currentThread().getPriority();
		State state = Thread.currentThread().getState();
		String threadGroupName = Thread.currentThread().getThreadGroup().getName();
		log.info("id=" + id + "; name=" + name + "; priority=" + priority + "; state=" + state + "; threadGroupName="
				+ threadGroupName);
	}
}