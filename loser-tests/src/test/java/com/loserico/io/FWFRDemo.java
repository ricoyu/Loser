package com.loserico.io;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FWFRDemo {
	final static String MSG = "Test message";

	public static void main(String[] args) throws IOException {
		try (FileWriter fw = new FileWriter("tempfilewriter")) {
			fw.write(MSG, 0, MSG.length());
		}
		char[] buf = new char[MSG.length()];
		try (FileReader fr = new FileReader("tempfilewriter")) {
			fr.read(buf, 0, MSG.length());
			System.out.println(buf);
		}
	}
}