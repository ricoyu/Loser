package org.loser.serializer.kryo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.loser.serializer.Serializer;

public class MemoryLeakTest {

	public static void main(String[] args) {
		Serializer serializer = new KryoSerializer();
		String s = "a";

		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		for (int i = 0; i < 1000; i++) {
			executorService.execute(() -> {
				serializer.toBytes(s);
			});
		}
	}
}
