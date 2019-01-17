package com.loserico.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

/**
 * We begin by running SerializationUtility.java, 
 * which saves (serializes) the AppleProduct object 
 * into a String instance, encoding the bytes using Base64.
 * Then, using that String as an argument for the deserialization method, 
 * we run DeserializationUtility.java, which reassembles (deserializes) 
 * the AppleProduct object from the given String.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-10-23 09:53
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class DeserializationUtility {

	/*
	 * serializedObj是SerializationUtility序列化成的字符串 这里可以反序列化回来
	 * 但是如果在反序列化之前将AppleProduct的serialVersionUID修改一下, 看看会发生什么
	 * 
	 * Now, let’s modify the serialVersionUID constant in AppleProduct.java, and
	 * reattempt to deserialize the AppleProduct object from the same String
	 * produced earlier. Re-running DeserializationUtility.java should generate
	 * this output.
	 * 
	 * 会抛出如下异常: Exception in thread "main" java.io.InvalidClassException:
	 * com.loserico.io.AppleProduct; local class incompatible: stream classdesc
	 * serialVersionUID = 7524559242837253281, local class serialVersionUID =
	 * 7524559242837253282
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String serializedObj = "rO0ABXNyABxjb20ubG9zZXJpY28uaW8uQXBwbGVQcm9kdWN0aGyaw7iyXKECAAJMAA1oZWFkcGhvbmVQb3J0dAASTGphdmEvbGFuZy9TdHJpbmc7TAAPdGh1bmRlcmJvbHRQb3J0cQB+AAF4cHQAEWhlYWRwaG9uZVBvcnQyMDIwdAATdGh1bmRlcmJvbHRQb3J0MjAyMA==";
		System.out.println("Deserializing AppleProduct...");

		AppleProduct deserializedObj = (AppleProduct) deSerializeObjectFromString(serializedObj);

		System.out.println("Headphone port of AppleProduct:" + deserializedObj.getHeadphonePort());
		System.out.println("Thunderbolt port of AppleProduct:" + deserializedObj.getThunderboltPort());
	}

	public static Object deSerializeObjectFromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}
}