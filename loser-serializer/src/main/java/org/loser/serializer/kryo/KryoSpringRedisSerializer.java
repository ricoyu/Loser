package org.loser.serializer.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSpringRedisSerializer<T> implements RedisSerializer<T> {

	private static final Set<Class<?>> CLASSES = new ConcurrentSkipListSet<Class<?>>();

	// 是否启用压缩
	private final boolean compress;

	private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {

		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.setCopyReferences(false);
			for (Class<?> clazz : CLASSES) {
				kryo.register(clazz);
			}
			return kryo;
		}
	};

	public KryoSpringRedisSerializer() {
		this.compress = false;
	}

	public KryoSpringRedisSerializer(boolean compress) {
		this.compress = compress;
	}

	public KryoSpringRedisSerializer(List<Class<?>> classes) {
		this.compress = false;
		for (Class<?> clazz : classes) {
			CLASSES.add(clazz);
		}
	}
	
	public KryoSpringRedisSerializer(boolean compress, List<Class<?>> classes) {
		this.compress = compress;
		for (Class<?> clazz : classes) {
			CLASSES.add(clazz);
		}
	}

	@Override
	public byte[] serialize(T object) throws SerializationException {
		if (object == null) {
			return null;
		}

		Kryo kryo = kryoThreadLocal.get();
		ByteArrayOutputStream byteArrayOutputStream = null;
		if (compress) {
			byteArrayOutputStream = new ByteArrayOutputStream();
			DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
			Output output = new Output(deflaterOutputStream);
			kryo.writeClassAndObject(output, object);
			output.close();
		} else {
			byteArrayOutputStream = new ByteArrayOutputStream(16384);
			Output output = new Output(byteArrayOutputStream);
			kryo.writeClassAndObject(output, object);
			output.close();
		}
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		InputStream in = new ByteArrayInputStream(bytes);
		if (compress) {
			in = new InflaterInputStream(in);
		}
		Input input = new Input(in, bytes.length);
		Kryo kryo = kryoThreadLocal.get();
		T obj = (T)kryo.readClassAndObject(input);
		input.close();
		return obj;
	}

	public void register(Class<?> clazz) {
		kryoThreadLocal.get().register(clazz);
	}
	
	public void register(Class<T> clazz, Serializer<T> serializer) {
		kryoThreadLocal.get().register(clazz, serializer);
	}
}
