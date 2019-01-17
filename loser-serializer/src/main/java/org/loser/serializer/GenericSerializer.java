package org.loser.serializer;

public interface GenericSerializer {

	/**
	 * 将对象序列化成byte[],如果object本身为null，则返回null
	 * @param object
	 * @return byte[]
	 */
	byte[] toBytes(Object object);

	/**
	 * 将byte[]反序列化成对象T。如果bytes为null或者其长度为0，返回null。如果clazz为null，抛 <code>NullPointerException</code>
	 * @param bytes
	 * @param clazz
	 * @return T
	 */
	<T> T toObject(byte[] bytes, Class<T> clazz);

}