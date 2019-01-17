package org.loser.serializer.kryo;

import static java.nio.charset.StandardCharsets.*;

import java.nio.charset.Charset;

import org.junit.Test;
import org.loser.serializer.Serializer;

public class StringSerializeTest {

	@Test
	public void testStringSerializePerformence() {
		String str = "asdasdad你好&%^%#(&)&數碼化教學模式以遊戲進行練習， 真正做到學、玩同時結合にほんご Nihongoעִבְרִית 'Ivrit";
		Serializer serializer = new KryoSerializer();
		((KryoSerializer)serializer).register(String.class);

		for (int j = 0; j < 100; j++) {
			long begin = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				byte[] data = str.getBytes(UTF_8);
//				System.out.println(new String(data, UTF_8));
				new String(data, UTF_8);
			}
			long end = System.currentTimeMillis();
			System.out.println("String.getBytes(...)花了 [" + (end - begin) + "] 毫秒");
			begin = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				byte[] data = serializer.toBytes(str);
//				System.out.println(serializer.toObject(data, String.class));
				serializer.toObject(data, String.class);
			}
			end = System.currentTimeMillis();
			System.out.println("serializer.toBytes(...)花了 [" + (end - begin) + "] 毫秒");
		}
/*		for (int j = 0; j < 100; j++) {
			long begin = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				str.getBytes(UTF_8);
			}
			long end = System.currentTimeMillis();
			System.out.println("String.getBytes(...)花了 [" + (end - begin) + "] 毫秒");
			begin = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				serializer.toBytes(str);
			}
			end = System.currentTimeMillis();
			System.out.println("serializer.toBytes(...)花了 [" + (end - begin) + "] 毫秒");
		}
*/	}
}
