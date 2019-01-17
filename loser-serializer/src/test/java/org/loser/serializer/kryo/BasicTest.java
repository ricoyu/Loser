package org.loser.serializer.kryo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.loser.serializer.Serializer;
import org.loser.serializer.jackson2.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BasicTest {

	@Test
	public void testSingleObject() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		Output output = new Output(new FileOutputStream("file.bin"));
		KryoUser user = new KryoUser();
		user.setBirthday(LocalDateTime.now());
		user.setName("kryo");
		kryo.writeObject(output, user);
		output.close();

		Input input = new Input(new FileInputStream("file.bin"));
		KryoUser kryoUser = kryo.readObject(input, KryoUser.class);
		input.close();

		System.out.println(kryoUser.getName());
		System.out.println(kryoUser.getBirthday());
	}

	@Test
	public void testObjectReference() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		Output output = new Output(new FileOutputStream("file2.bin"));
		KryoAddress address = new KryoAddress();
		address.setAddress("苏州园区");
		KryoUserWithAddress user = new KryoUserWithAddress();
		user.setBirthday(LocalDateTime.now());
		user.setName("kryo");
		user.setAddress(address);

		kryo.writeObject(output, user);
		output.close();

		Input input = new Input(new FileInputStream("file2.bin"));
		KryoUserWithAddress kryoUser = kryo.readObject(input, KryoUserWithAddress.class);
		input.close();

		System.out.println(kryoUser.getName());
		System.out.println(kryoUser.getBirthday());
		System.out.println(kryoUser.getAddress());
		System.out.println(kryoUser.getAddress().getAddress());
	}

	@Test
	public void testSelfReference() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		kryo.register(KryoUserWithAddressAndChild.class);
		Output output = new Output(new FileOutputStream("file3.bin"));
		KryoAddress address = new KryoAddress();
		address.setAddress("苏州园区");

		KryoUserWithAddressAndChild user = new KryoUserWithAddressAndChild();
		user.setBirthday(LocalDateTime.now());
		user.setName("kryo");
		user.setAddress(address);

		KryoUserWithAddressAndChild child = new KryoUserWithAddressAndChild();
		child.setName("讨厌鬼");
		child.setBirthday(LocalDateTime.now());
		user.setChild(child);

		kryo.writeObject(output, user);
		output.close();

		Input input = new Input(new FileInputStream("file3.bin"));
		KryoUserWithAddressAndChild kryoUser = kryo.readObject(input, KryoUserWithAddressAndChild.class);
		input.close();

		System.out.println(kryoUser.getName());
		System.out.println(kryoUser.getBirthday());
		System.out.println(kryoUser.getAddress());
		System.out.println(kryoUser.getAddress().getAddress());
		System.out.println(kryoUser.getChild());
		System.out.println(kryoUser.getChild().getName());
		System.out.println(kryoUser.getChild().getBirthday());

		KryoSerializer serializer = new KryoSerializer();
		byte[] bytes = serializer.toBytes(user);
		KryoUserWithAddressAndChild ahtoherUser = serializer.toObject(bytes, KryoUserWithAddressAndChild.class);
		System.out.println(ahtoherUser.getName());
		System.out.println(ahtoherUser.getBirthday());
		System.out.println(ahtoherUser.getAddress());
		System.out.println(ahtoherUser.getAddress().getAddress());
		System.out.println(ahtoherUser.getChild());
		System.out.println(ahtoherUser.getChild().getName());
		System.out.println(ahtoherUser.getChild().getBirthday());

	}

	@Test
	public void testRecursiveReference() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		kryo.register(KryoUserWithAddressAndChildFather.class);
		Output output = new Output(new FileOutputStream("file4.bin"));
		KryoAddress address = new KryoAddress();
		address.setAddress("苏州园区");

		KryoUserWithAddressAndChildFather user = new KryoUserWithAddressAndChildFather();
		user.setBirthday(LocalDateTime.now());
		user.setName("kryo");
		user.setAddress(address);

		KryoUserWithAddressAndChildFather child = new KryoUserWithAddressAndChildFather();
		child.setName("讨厌鬼");
		child.setBirthday(LocalDateTime.now());
		user.setChild(child);

		child.setFather(user);

		kryo.writeObject(output, user);
		output.close();

		Input input = new Input(new FileInputStream("file4.bin"));
		KryoUserWithAddressAndChildFather kryoUser = kryo.readObject(input, KryoUserWithAddressAndChildFather.class);
		input.close();

		System.out.println(kryoUser.getName());
		System.out.println(kryoUser.getBirthday());
		System.out.println(kryoUser.getAddress());
		System.out.println(kryoUser.getAddress().getAddress());
		System.out.println(kryoUser.getChild());
		System.out.println(kryoUser.getChild().getName());
		System.out.println(kryoUser.getChild().getBirthday());

		KryoSerializer serializer = new KryoSerializer();
		byte[] bytes = serializer.toBytes(user);

		KryoUserWithAddressAndChildFather ahtoherUser = serializer.toObject(bytes,
				KryoUserWithAddressAndChildFather.class);
		System.out.println(ahtoherUser.getName());
		System.out.println(ahtoherUser.getBirthday());
		System.out.println(ahtoherUser.getAddress());
		System.out.println(ahtoherUser.getAddress().getAddress());

		System.out.println(ahtoherUser.getChild());
		System.out.println(ahtoherUser.getChild().getFather().getChild());
		System.out.println(ahtoherUser.getChild().getName());
		System.out.println(ahtoherUser.getChild().getBirthday());

		System.out.println(ahtoherUser.getChild().getFather());
		System.out.println(ahtoherUser.getChild().getFather().getName());
		System.out.println(ahtoherUser.getChild().getFather().getBirthday());

	}

	@Test
	public void testConcurrentRecursiveReference() throws FileNotFoundException {
		ExecutorService executorService = Executors.newCachedThreadPool();

		for (int i = 0; i < 100; i++) {
			executorService.execute(() -> {
				try {
					Kryo kryo = new Kryo();
					kryo.register(KryoUserWithAddressAndChildFather.class);
					Output output = new Output(new FileOutputStream("file4.bin"));
					KryoAddress address = new KryoAddress();
					address.setAddress("苏州园区");

					KryoUserWithAddressAndChildFather user = new KryoUserWithAddressAndChildFather();
					user.setBirthday(LocalDateTime.now());
					user.setName("kryo");
					user.setAddress(address);

					KryoUserWithAddressAndChildFather child = new KryoUserWithAddressAndChildFather();
					child.setName("讨厌鬼");
					child.setBirthday(LocalDateTime.now());
					user.setChild(child);

					child.setFather(user);

					kryo.writeObject(output, user);
					output.close();

					Input input = new Input(new FileInputStream("file4.bin"));
					KryoUserWithAddressAndChildFather kryoUser = kryo.readObject(input,
							KryoUserWithAddressAndChildFather.class);
					input.close();

					System.out.println(kryoUser.getName());
					System.out.println(kryoUser.getBirthday());
					System.out.println(kryoUser.getAddress());
					System.out.println(kryoUser.getAddress().getAddress());
					System.out.println(kryoUser.getChild());
					System.out.println(kryoUser.getChild().getName());
					System.out.println(kryoUser.getChild().getBirthday());

					KryoSerializer serializer = new KryoSerializer();
					byte[] bytes = serializer.toBytes(user);

					KryoUserWithAddressAndChildFather ahtoherUser = serializer.toObject(bytes,
							KryoUserWithAddressAndChildFather.class);
					System.out.println(ahtoherUser.getName());
					System.out.println(ahtoherUser.getBirthday());
					System.out.println(ahtoherUser.getAddress());
					System.out.println(ahtoherUser.getAddress().getAddress());

					System.out.println(ahtoherUser.getChild());
					System.out.println(ahtoherUser.getChild().getFather().getChild());
					System.out.println(ahtoherUser.getChild().getName());
					System.out.println(ahtoherUser.getChild().getBirthday());

					System.out.println(ahtoherUser.getChild().getFather());
					System.out.println(ahtoherUser.getChild().getFather().getName());
					System.out.println(ahtoherUser.getChild().getFather().getBirthday());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

	}

	@Test
	public void testKryoVsJackson2() {
		Serializer kryo = new KryoSerializer();
		//		Serializer json = new Jackson2JsonRedisSerializer();
		//		RedisSerializer<Object> redisSerializer = new GenericJackson2JsonRedisSerializer();
		RedisSerializer<Object> redisSerializer = new KryoSpringRedisSerializer();

		((KryoSerializer) kryo).register(KryoUserWithAddressAndChildFather.class);
		KryoAddress address = new KryoAddress();
		address.setAddress("苏州园区");

		KryoUserWithAddressAndChildFather user = new KryoUserWithAddressAndChildFather();
		user.setBirthday(LocalDateTime.now());
		user.setName("kryo");
		user.setAddress(address);

		KryoUserWithAddressAndChildFather child = new KryoUserWithAddressAndChildFather();
		child.setName("讨厌鬼");
		child.setBirthday(LocalDateTime.now());
		user.setChild(child);

		child.setFather(user);

		byte[] data = kryo.toBytes(user);
		KryoUserWithAddressAndChildFather restoredUser = kryo.toObject(data, KryoUserWithAddressAndChildFather.class);

		System.out.println(restoredUser.getName());
		System.out.println(restoredUser.getBirthday());
		System.out.println(restoredUser.getAddress());
		System.out.println(restoredUser.getAddress().getAddress());
		System.out.println(restoredUser.getChild());
		System.out.println(restoredUser.getChild().getName());
		System.out.println(restoredUser.getChild().getBirthday());
		System.out.println("restoredUser.getChild().getFather().getChild().getFather().getName(): "
				+ restoredUser.getChild().getFather().getChild().getFather().getName());

		System.out.println("======================================");

		byte[] kryoSpringSerializerData = redisSerializer.serialize(user);

		KryoUserWithAddressAndChildFather springUser = (KryoUserWithAddressAndChildFather) redisSerializer.deserialize(
				kryoSpringSerializerData);
		System.out.println(springUser.getName());
		System.out.println(springUser.getBirthday());
		System.out.println(springUser.getAddress());
		System.out.println(springUser.getAddress().getAddress());
		System.out.println(springUser.getChild());
		System.out.println(springUser.getChild().getName());
		System.out.println(springUser.getChild().getBirthday());

		/*
		 * byte[] dataJson = json.toBytes(user); KryoUserWithAddressAndChildFather
		 * jsonUser = json.toObject(dataJson,
		 * KryoUserWithAddressAndChildFather.class);
		 * System.out.println(jsonUser.getName());
		 * System.out.println(jsonUser.getBirthday());
		 * System.out.println(jsonUser.getAddress());
		 * System.out.println(jsonUser.getAddress().getAddress());
		 * System.out.println(jsonUser.getChild());
		 * System.out.println(jsonUser.getChild().getName());
		 * System.out.println(jsonUser.getChild().getBirthday());
		 */

	}
}
