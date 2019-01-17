package com.loserico.security.util;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.shiro.util.SimpleByteSource;

public class ByteSource extends SimpleByteSource implements Serializable {

	public ByteSource(byte[] bytes) {
		super(bytes);
	}

	public ByteSource(org.apache.shiro.util.ByteSource source) {
		super(source);
	}

	public ByteSource(char[] chars) {
		super(chars);
	}

	public ByteSource(File file) {
		super(file);
	}

	public ByteSource(InputStream stream) {
		super(stream);
	}

	public ByteSource(String string) {
		super(string);
	}

	private static final long serialVersionUID = 5174243754908925894L;

	@Override
	public byte[] getBytes() {
		return super.getBytes();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public String toHex() {
		return super.toHex();
	}

	@Override
	public String toBase64() {
		return super.toBase64();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
