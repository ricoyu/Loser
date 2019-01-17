package com.loserico.concurrent.chapter4;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The output of this code will look like this:
 * 
 * TL2: Update [author=Author [name=Ben], updateText=I like pie, createTime=0],
 * Update [author=Author [name=Charles], updateText=I like ham on rye,
 * createTime=0], Update [author=Author [name=Jeffrey], updateText=I like a lot
 * of things, createTime=0], Update [author=Author [name=Gavin], updateText=I
 * like otters, createTime=0],
 * 
 * TL1: Update [author=Author [name=Ben], pdateText=I like pie, createTime=0],
 * Update [author=Author [name=Charles], updateText=I like ham on rye,
 * createTime=0], Update [author=Author [name=Jeffrey], updateText=I like a lot
 * of things, createTime=0],
 * 
 * As you can see, the second output line (tagged as TL1) is missing the final
 * update (the one that mentions otters), despite the fact that the latching
 * meant that mbex1 was accessed after the list had been modified. This
 * demonstrates that the Iterator contained in mbex1 was copied by mbex2, and
 * that the addition of the final update was invisible to mbex1. This is the
 * copy-on-write property that we want these objects to display.
 * 
 * Performance of CopyOnWriteArrayList The use of the CopyOnWriteArrayList
 * class does require a bit more thought than using ConcurrentHashMap, which
 * really is a drop-in concurrent replacement for HashMap. This is because of
 * performance issues—the copy-on-write property means that if the list is
 * altered while a read or a traversal is taking place, the entire array must
 * be copied. This means that if changes to the list are common, compared to
 * read accesses, this approach won’t necessarily yield high performance. But
 * as we’ll say repeatedly in chapter 6, the only way to reliably get
 * well-performing code is to test, retest, and measure the results.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class CopyOnWriteExampleMain {

	public static void main(String[] a) {
		final CountDownLatch firstLatch = new CountDownLatch(1);
		final CountDownLatch secondLatch = new CountDownLatch(1);
		final Update.Builder updateBuilder = new Update.Builder();

		/*
		 * If the CopyOnWriteArrayList was replaced with an ordinary List, the
		 * result would be a ConcurrentModificationException.
		 */
		final CopyOnWriteArrayList<Update> list = new CopyOnWriteArrayList<>();
		list.add(updateBuilder.author(new Author("Ben")).updateText("I like pie").build());
		list.add(updateBuilder.author(new Author("Charles")).updateText("I like ham on rye").build());

		/*
		 * This is also an example of a Lock object being shared between two
		 * threads to control access to a shared resource (in this case,
		 * STDOUT). This code would be much messier if expressed in the
		 * block-structured view.
		 */
		Lock lock = new ReentrantLock();
		final MicroBlogTimeline timeline1 = new MicroBlogTimeline("TL1", list, lock);
		final MicroBlogTimeline timeline2 = new MicroBlogTimeline("TL2", list, lock);

		Thread t1 = new Thread() {
			public void run() {
				list.add(updateBuilder.author(new Author("Jeffrey")).updateText("I like a lot of things").build());
				timeline1.prep();
				firstLatch.countDown();
				try {
					// Enforce strict event ordering with latches
					secondLatch.await();
				} catch (InterruptedException e) {
				}
				timeline1.printTimeline();
			}
		};

		Thread t2 = new Thread() {
			public void run() {
				try {
					// Enforce strict event ordering with latches
					firstLatch.await();
					//t1中看不到这一条数据
					list.add(updateBuilder.author(new Author("Gavin")).updateText("I like otters") .build());
					timeline2.prep();
					secondLatch.countDown();
				} catch (InterruptedException e) {
				}
				timeline2.printTimeline();
			}
		};
		t1.start();
		t2.start();
	}

}
