package com.loserico.concurrent.test;

import java.util.LinkedList;
import java.util.List;

/**
 * In this section we are going to implement a very basic and simple blocking Queue.
 * This queue should do nothing more than hold the elements we have put into it and
 * give them back when calling get(). The get() should block until a new element is
 * available.
 * 
 * It is clear that the java.util.concurrent package already provides such
 * functionality and that there is no need to implement this again, but for
 * demonstration purposes we do it here in order to show how to test such a class.
 * 
 * As backing data structure for our queue we choose a standard LinkedList from the
 * java.util package. This list is not synchronized and call its get() method does
 * not block. Hence we have to synchronized access to the list and we have to add
 * the blocking functionality. The latter can be implemented with a simple while()
 * loop that calls the wait() method on the list, when the queue is empty. If the
 * queue is not empty, it returns the first element
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 * @param <T>
 */
public class SimpleBlockingQueue<T> {
	private List<T> queue = new LinkedList<T>();

	public int getSize() {
		synchronized (queue) {
			return queue.size();
		}
	}

	public void put(T obj) {
		synchronized (queue) {
			queue.add(obj);
			queue.notify();
		}
	}

	public T get() throws InterruptedException {
		while (true) {
			synchronized (queue) {
				if (queue.isEmpty()) {
					queue.wait();
				} else {
					return queue.remove(0);
				}
			}
		}
	}
}