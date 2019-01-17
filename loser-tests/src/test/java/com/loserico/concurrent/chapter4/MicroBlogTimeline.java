package com.loserico.concurrent.chapter4;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Consider a timeline of microblogging updates. This is a classic example of
 * data that isn’t 100 percent mission-critical and where a performant,
 * self-consistent snapshot for each reader is preferred over total global
 * consistency. This listing shows a holder class that represents an individual
 * user’s view of their timeline.
 * 
 * This class is specifically designed to illustrate the behavior of an
 * Iterator under copyon- write semantics. You need to introduce locking in the
 * print method to prevent the output being jumbled between the two threads,
 * and to allow you to see the separate state of the two threads.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class MicroBlogTimeline {
	private final CopyOnWriteArrayList<Update> updates;
	private final Lock lock;
	private final String name;
	private Iterator<Update> it;

	public MicroBlogTimeline(String name_) {
		name = name_;
		updates = new CopyOnWriteArrayList<>();
		lock = new ReentrantLock();
	}

	MicroBlogTimeline(String name, CopyOnWriteArrayList<Update> updates, Lock lock) {
		this.name = name;
		this.updates = updates;
		this.lock = lock;
	}

	public void addUpdate(Update update) {
		updates.add(update);
	}

	public void prep() {
		it = updates.iterator();
	}

	public void printTimeline() {
		lock.lock();
		try {
			if (it != null) {
				System.out.print(name + ": ");
				while (it.hasNext()) {
					Update s = it.next();
					System.out.print(s + ", ");
				}
				System.out.println();
			}
		} finally {
			lock.unlock();
		}
	}
}