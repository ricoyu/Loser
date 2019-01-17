package com.loserico.concurrent.chapter4;

/**
 * In this version, as well as recording the time of the last update, each node
 * that receives an update informs another node of that receipt.
 * 
 * This is a naïve attempt to build a multithreaded update handling system.
 * It’s designed to demonstrate deadlocking—you shouldn’t use this as the basis
 * for real code.
 * 
 * At first glance, this code looks sensible. You have two updates being sent
 * to separate threads, each of which has to be confirmed on backup threads.
 * This doesn’t seem too outlandish a design—if one thread has a failure, there
 * is another thread that can potentially carry on.
 * 
 * If you run the code, you’ll normally see an example of a deadlock—both
 * threads will report receiving the update, but neither will confirm receiving
 * the update for which they’re the backup thread. The reason for this is that
 * each thread requires the other to release the lock it holds before the
 * confirmation method can progress.
 * 
 * To deal with deadlocks, one technique is to always acquire locks in the same
 * order in every thread.
 * 
 * In this example, the first thread to start acquires them in the order A, B,
 * whereas the second thread acquires them in the order B, A. If both threads
 * had insisted on acquiring in order A, B, the deadlock would have been
 * avoided, because the second thread would have been blocked from running at
 * all until the first had completed and released its locks.
 * 
 * In terms of the fully synchronized object approach, this deadlock is
 * prevented because the code violates the consistent state rule. When a
 * message arrives, the receiving node calls another object while the message
 * is still being processed—the state isn’t consistent when it makes this call.
 * 
 * We asked earlier, what is it that’s being synchronized in the code in
 * listing 4.1? The answer is: The memory representation in different threads
 * of the object being locked is what is being synchronized. That is, after the
 * synchronized block (or method) has completed, any and all changes that were
 * made to the object being locked are flushed back to main memory before the
 * lock is released,
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class DeadlockMicroBlogNode implements SimpleMicroBlogNode {

	private final String ident;

	public DeadlockMicroBlogNode(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public synchronized void propagateUpdate(Update update, SimpleMicroBlogNode backup) {
		System.out.println(ident + ": recvd: " + update.getUpdateText() + " ; backup: " + backup.getIdent());
		backup.confirmUpdate(this, update);
	}

	@Override
	public synchronized void confirmUpdate(SimpleMicroBlogNode other, Update update) {
		System.out.println(ident + ": recvd confirm: " + update.getUpdateText() + " from " + other.getIdent());
	}

	public static void main(String[] args) {
		final DeadlockMicroBlogNode local = new DeadlockMicroBlogNode("localhost:8888");
		final DeadlockMicroBlogNode other = new DeadlockMicroBlogNode("localhost:8988");
		final Update first = getUpdate("1");
		final Update second = getUpdate("2");

		// First update sent to first thread
		new Thread(new Runnable() {
			public void run() {
				local.propagateUpdate(first, other);
			}
		}).start();

		/*
		 * Second update sent to other thread
		 */
		new Thread(new Runnable() {
			public void run() {
				other.propagateUpdate(second, local);
			}
		}).start();
	}

	private static Update getUpdate(String s) {
		Update.Builder b = new Update.Builder();
		b.updateText(s).author(new Author("Ben"));
		return b.build();
	}

}