package org.loser.serializer.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.loser.serializer.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @of
 * Kryo序列化/反序列化工具,操作的对象无需实现Serializable接口 <br/> 
 * 该工具类线程安全
 * 
 * @on
 * @author Rico Yu
 * @since 2016-11-07 15:22
 * @version 1.0
 *
 */
public class KryoSerializer implements Serializer {

	private Charset charset = Charset.forName("UTF-8");

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

	public KryoSerializer() {
		this.compress = false;
	}

	public KryoSerializer(Charset charset) {
		this.compress = false;
		this.charset = charset;
	}

	public KryoSerializer(boolean compress) {
		this.compress = compress;
	}

	public KryoSerializer(List<Class<?>> classes) {
		this.compress = false;
		for (Class<?> clazz : classes) {
			CLASSES.add(clazz);
		}
	}

	public KryoSerializer(boolean compress, Charset charset) {
		this.compress = compress;
		this.charset = charset;
	}

	@Override
	public byte[] toBytes(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof String) {
			return ((String) object).getBytes(charset);
		}

		Kryo kryo = kryoThreadLocal.get();
		ByteArrayOutputStream byteArrayOutputStream = null;
		if (compress) {
			byteArrayOutputStream = new ByteArrayOutputStream();
			DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
			Output output = new Output(deflaterOutputStream);
			output.close();
		} else {
			byteArrayOutputStream = new ByteArrayOutputStream(16384);
			Output output = new Output(byteArrayOutputStream);
			kryo.writeObject(output, object);
			output.close();
		}
		return byteArrayOutputStream.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T toObject(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		Objects.requireNonNull(clazz, "参数clazz不能为null!");

		if (clazz.isAssignableFrom(String.class)) {
			return (T) new String(bytes, charset);
		}

		InputStream in = new ByteArrayInputStream(bytes);
		if (compress) {
			in = new InflaterInputStream(in);
		}
		Input input = new Input(in, bytes.length);
		Kryo kryo = kryoThreadLocal.get();
		T obj = kryo.readObject(input, clazz);
		input.close();
		return obj;
	}

	/**
	 * 将当前线程的kryo对象从ThreadLocal中删除，防止内存泄漏。
	 * 注意在容器中(Tomcat等)使用时没有必要调用该方法，因为容器多采用线程池机制，线程都是复用的
	 * 但如果自己代码里手工创建线程并在其中使用KryoSerializer，最好在线程销毁前调用一下该方法。
	 */
	public void remove() {
		kryoThreadLocal.remove();
	}

	public void register(Class<?> clazz) {
		kryoThreadLocal.get().register(clazz);
	}
}