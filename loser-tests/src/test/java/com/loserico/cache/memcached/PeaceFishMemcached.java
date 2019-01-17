package com.loserico.cache.memcached;

import java.util.Date;

import org.loser.serializer.Serializer;
import org.loser.serializer.kryo.KryoSerializer;

import com.whalin.MemCached.MemCachedClient;

public class PeaceFishMemcached implements Memcached {

	private MemCachedClient memCachedClient;

	private final Serializer serializer = new KryoSerializer();

	public PeaceFishMemcached(MemCachedClient memCachedClient) {
		this.memCachedClient = memCachedClient;
	}

	@Override
	public boolean add(String key, Object value) {
		return memCachedClient.add(key, serializer.toBytes(value));
	}

	@Override
	public <T> T get(String key, Class<T> clazz) {
		byte[] data = (byte[]) memCachedClient.get(key);
		if (data != null && data.length != 0) {
			return serializer.toObject(data, clazz);
		}
		return null;
	}

	@Override
	public boolean add(String key, Object value, long ttl) {
		long time = new Date().getTime() + ttl;
		return memCachedClient.add(key, serializer.toBytes(value), new Date(time));
	}

	@Override
	public boolean set(String key, Object value, long ttl) {
		long time = new Date().getTime() + ttl;
		return memCachedClient.set(key, serializer.toBytes(value), new Date(time));
	}

}
