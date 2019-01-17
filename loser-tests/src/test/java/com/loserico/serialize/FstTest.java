package com.loserico.serialize;

import java.time.LocalDate;

import org.junit.Test;
import org.loser.serializer.utils.SerializationUtils;
import org.nustaq.serialization.FSTConfiguration;

/**
 * https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
 * https://dzone.com/articles/serialization-proxy-pattern
 * <p>
 * Copyright: Copyright (c) 2018-03-10 08:54
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class FstTest {

	@Test
	public void testSeriableObject() {
		User user = new User();
		user.setBirthday(LocalDate.of(1982, 11, 9));
		user.setName("俞雪华");
		user.setId(1);
		
		FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();
		byte[] bytes = fstConfig.asByteArray(user);
		
		User user2 = (User)fstConfig.asObject(bytes);
		System.out.println(user2.getName() + " " + user2.getBirthday());
		
		User user3 = SerializationUtils.deserialize(bytes, User.class);
		System.out.println(user3.getName() + " " + user3.getBirthday());
	}
	
	@Test
	public void testNonSeriableObject() {
		UserNonSerializable user = new UserNonSerializable();
		user.setBirthday(LocalDate.of(1982, 11, 9));
		user.setName("俞雪华");
		user.setId(1);
		
		FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();
		byte[] bytes = fstConfig.asByteArray(user);
		
		UserNonSerializable user2 = (UserNonSerializable)fstConfig.asObject(bytes);
		System.out.println(user2.getName() + " " + user2.getBirthday());
	}
}
