package com.loserico.netty.helloworld;

import java.nio.ByteBuffer;

/**
 * The LoopBackTimeStamp class has 2 long numbers. it also has 2 methods,
 * toByteArray() is used to transfer the internal 2 long number into a byte array of
 * 16 bytes. fromByteArray() works reversely, change 16 bytes array back to the 2 long
 * numbers.
 * 
 * @author Rico Yu
 * @since 2016-12-20 11:11
 * @version 1.0
 *
 */
public class LoopBackTimeStamp {
	private long sendTimeStamp;
	private long recvTimeStamp;

	public LoopBackTimeStamp() {
		this.setSendTimeStamp(System.nanoTime());
	}

	public long timeLapseInNanoSecond() {
		return recvTimeStamp - getSendTimeStamp();
	}

	/**
	 * Transfer 2 long number to a 16 byte-long byte[], every 8 bytes represent a long
	 * number.
	 * 
	 * @return
	 */
	public byte[] toByteArray() {

		/*
		 * Long占用的bit数除以Byte占用的bit数 = Long占用的Byte数
		 * 即Long类型占几个byte
		 */
		final int byteOfLong = Long.SIZE / Byte.SIZE;
		byte[] ba = new byte[byteOfLong * 2];
		byte[] t1 = ByteBuffer.allocate(byteOfLong).putLong(getSendTimeStamp()).array();
		byte[] t2 = ByteBuffer.allocate(byteOfLong).putLong(recvTimeStamp).array();

		for (int i = 0; i < byteOfLong; i++) {
			ba[i] = t1[i];
		}

		for (int i = 0; i < byteOfLong; i++) {
			ba[i + byteOfLong] = t2[i];
		}
		return ba;
	}

	/**
	 * Transfer a 16 byte-long byte[] to 2 long numbers, every 8 bytes represent a
	 * long number.
	 * 
	 * @param content
	 */
	public void fromByteArray(byte[] content) {
		int len = content.length;
		final int byteOfLong = Long.SIZE / Byte.SIZE;
		if (len != byteOfLong * 2) {
			System.out.println("Error on content length");
			return;
		}
		ByteBuffer buf1 = ByteBuffer.allocate(byteOfLong).put(content, 0, byteOfLong);
		ByteBuffer buf2 = ByteBuffer.allocate(byteOfLong).put(content, byteOfLong, byteOfLong);
		buf1.rewind();
		buf2.rewind();
		this.setSendTimeStamp(buf1.getLong());
		this.recvTimeStamp = buf2.getLong();
	}

	public long getRecvTimeStamp() {
		return recvTimeStamp;
	}

	public void setRecvTimeStamp(long recvTimeStamp) {
		this.recvTimeStamp = recvTimeStamp;
	}

	public long getSendTimeStamp() {
		return sendTimeStamp;
	}

	public void setSendTimeStamp(long sendTimeStamp) {
		this.sendTimeStamp = sendTimeStamp;
	}

	// getter/setter ignored
}