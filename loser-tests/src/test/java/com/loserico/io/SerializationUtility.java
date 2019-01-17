package com.loserico.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Next, we’ll need two utility classes: 
 * one to serialize an AppleProduct object into a String, 
 * and another to deserialize the object from that String:
 * 
 * <p>
 * Copyright: Copyright (c) 2018-10-23 09:51
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class SerializationUtility {

	public static void main(String[] args) throws IOException {
		AppleProduct macBook = new AppleProduct();
		macBook.headphonePort = "headphonePort2020";
		macBook.thunderboltPort = "thunderboltPort2020";

		String serializedObj = serializeObjectToString(macBook);

		//Serialized AppleProduct object to string:
		//rO0ABXNyABxjb20ubG9zZXJpY28uaW8uQXBwbGVQcm9kdWN0aGyaw7iyXKECAAJMAA1oZWFkcGhvbmVQb3J0dAASTGphdmEvbGFuZy9TdHJpbmc7TAAPdGh1bmRlcmJvbHRQb3J0cQB+AAF4cHQAEWhlYWRwaG9uZVBvcnQyMDIwdAATdGh1bmRlcmJvbHRQb3J0MjAyMA==
		System.out.println("Serialized AppleProduct object to string:");
		System.out.println(serializedObj);
	}

	public static String serializeObjectToString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();

		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
}