package org.loser.serializer.kryo;

import java.time.LocalDateTime;

import org.junit.Test;

public class KryoSpringRedisSerializerTest {

	@Test
	public void testDeserializer() {
		KryoSpringRedisSerializer<LocalDateTime> serializer = new KryoSpringRedisSerializer<LocalDateTime>();
		byte[] bytes = serializer.serialize(LocalDateTime.now());
		LocalDateTime obj = serializer.deserialize(bytes);
		System.out.println(obj);
	}
}
