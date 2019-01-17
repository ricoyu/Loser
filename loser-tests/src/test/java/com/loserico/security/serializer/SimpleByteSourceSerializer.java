package com.loserico.security.serializer;

import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class SimpleByteSourceSerializer extends Serializer<SimpleByteSource> {

	@Override
	public void write(Kryo kryo, Output output, SimpleByteSource object) {
		if (object == null) {
			output.write(null);
		}
		output.write(object.getBytes());
		output.flush();
	}

	@Override
	public SimpleByteSource read(Kryo kryo, Input input, Class<SimpleByteSource> type) {
		byte[] bytes = input.getBuffer();
		return (SimpleByteSource) ByteSource.Util.bytes(bytes);
	}

}
