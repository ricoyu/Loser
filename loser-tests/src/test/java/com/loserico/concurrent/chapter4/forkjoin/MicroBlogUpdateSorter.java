package com.loserico.concurrent.chapter4.forkjoin;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

import com.loserico.concurrent.chapter4.Update;

/**
 * we have an array of updates to the microblogging service that may have
 * arrived at different times, and we want to sort them by their arrival times,
 * in order to generate timelines for the users, like the one you generated in
 * listing 4.9.
 * 
 * To achieve this, we’ll use a multithreaded sort, which is a variant of
 * MergeSort. Listing 4.16 uses a specialized subclass of ForkJoinTask—the
 * RecursiveAction. This is simpler than the general ForkJoinTask because it’s
 * explicit about not having any overall result (the updates will be reordered
 * in place), and it emphasizes the recursive nature of the tasks.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class MicroBlogUpdateSorter extends RecursiveAction {
	private static final long serialVersionUID = -4156174248787359970L;
	/*
	 * 32 or fewer sorted serially
	 */
	private static final int SMALL_ENOUGH = 32;
	private final Update[] updates;
	private final int start, end;
	private final Update[] result;

	public MicroBlogUpdateSorter(Update[] updates) {
		this(updates, 0, updates.length);
	}

	public MicroBlogUpdateSorter(Update[] updates, int start, int end) {
		this.start = start;
		this.end = end;
		this.updates = updates;
		this.result = new Update[updates.length];
	}

	private void merge(MicroBlogUpdateSorter left, MicroBlogUpdateSorter right) {
		int i = 0;
		int lCt = 0;
		int rCt = 0;
		while (lCt < left.size() && rCt < right.size()) {
			result[i++] = (left.result[lCt].compareTo(right.result[rCt]) < 0) ? left.result[lCt++] : right.result[rCt++];
		}
		while (lCt < left.size()) {
			result[i++] = left.result[lCt++];
		}
		while (rCt < right.size()) {
			result[i++] = right.result[rCt++];
		}
	}

	public int size() {
		return end - start;
	}

	public Update[] getResult() {
		return result;
	}

	/**
	 * The MicroBlogUpdateSorter class provides a way of ordering a list of
	 * updates using the compareTo() method on Update objects. The compute()
	 * method (which you have to implement because it’s abstract in the
	 * RecursiveAction superclass) basically orders an array of microblog
	 * updates by the time of creation of an update.
	 */
	@Override
	protected void compute() {
		if (size() < SMALL_ENOUGH) {
			System.arraycopy(updates, start, result, 0, size());
			Arrays.sort(result, 0, size());
		} else {
			int mid = size() / 2;
			MicroBlogUpdateSorter left = new MicroBlogUpdateSorter(updates, start, start + mid);
			MicroBlogUpdateSorter right = new MicroBlogUpdateSorter(updates, start + mid, end);
			invokeAll(left, right);
			merge(left, right);
		}
	}
}