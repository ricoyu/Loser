package com.loserico.concurrent.chapter4;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * Running this example as is shows how the queue will quickly fill, meaning
 * that the offering thread is outpacing the taking thread. Within a very short
 * time, the message “Unable to hand off Update to Queue due to timeout” will
 * start to appear.
 * 
 * This represents one extreme of the “connected thread pool” model—when the
 * upstream thread pool is running quicker than the downstream one. This can be
 * problematic, introducing such issues as an overflowing LinkedBlockingQueue.
 * Alternatively, if there are more consumers than producers, the queue can
 * empty. Fortunately Java 7 has a new twist on the BlockingQueue that can
 * help—the TransferQueue.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class MicroBlogThreadMainTransferQueue {

	public static void main(String[] a) {
		final Update.Builder builder = new Update.Builder();
		final TransferQueue<Update> queue = new LinkedTransferQueue<>();

		MicroBlogThreadTransferQueue t1 = new MicroBlogThreadTransferQueue(queue, 10) {
			public void doAction() {
				text = text + "X";
				Update update = builder.author(new Author("Tallulah")).updateText(text).build();
				boolean handed = false;
				try {
					handed = updatesQueue.tryTransfer(update, 100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
				}
				if (!handed) {
					System.out.println("Unable to handoff Update to Queue due to timeout");
				}
			}
		};

		MicroBlogThread t2 = new MicroBlogThread(queue, 1000) {
			public void doAction() {
				Update update = null;
				try {
					update = updatesQueue.take();
					System.out.println("t2: " + update.getUpdateText());
				} catch (InterruptedException e) {
					return;
				}
			}
		};
		t1.start();
		t2.start();
	}

}
