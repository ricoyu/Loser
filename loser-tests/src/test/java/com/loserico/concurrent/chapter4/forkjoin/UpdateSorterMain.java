package com.loserico.concurrent.chapter4.forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import com.loserico.concurrent.chapter4.Author;
import com.loserico.concurrent.chapter4.Update;

public class UpdateSorterMain {

	public static void main(String[] args) {
		List<Update> list = new ArrayList<Update>();
		String text = "";
		final Update.Builder builder = new Update.Builder();
		final Author author = new Author("Tallulah");

		for (int i = 0; i < 256; i++) {
			text = text + "X";
			long now = System.currentTimeMillis();
			list.add(builder.author(author).updateText(text).createTime(now).build());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		// 随机打乱原来的顺序，和洗牌一样
		Collections.shuffle(list);
		// Avoid allocation by passing zero-sized array
		Update[] updates = list.stream().toArray(Update[]::new);
		// Update[] updates = list.toArray(new Update[0]);
		MicroBlogUpdateSorter sorter = new MicroBlogUpdateSorter(updates);
		ForkJoinPool pool = new ForkJoinPool(4);
		pool.invoke(sorter);

		for (Update u : sorter.getResult()) {
			System.out.println(u);
		}
	}
}
