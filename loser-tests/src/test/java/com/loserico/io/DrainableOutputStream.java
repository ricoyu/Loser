package com.loserico.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * For any other type of OutputStream, not inherently supporting a similar
 * conversion to a byte[] object, there is no way to make the conversion, before
 * the OutputStream is drained, i.e. before the desired calls to its write()
 * methods have been completed. If such an assumption (of the writes to have
 * been completed) can be made, and if the original OutputStream object can be
 * replaced, then one option is to wrap it inside a delegate class that would
 * essentially "grab" the bytes that would be supplied via its write() methods.
 */
public class DrainableOutputStream extends FilterOutputStream {

	private final ByteArrayOutputStream buffer;

	public DrainableOutputStream(OutputStream out) {
		super(out);
		this.buffer = new ByteArrayOutputStream();
	}

	@Override
	public void write(byte b[]) throws IOException {
		this.buffer.write(b);
		super.write(b);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		this.buffer.write(b, off, len);
		super.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		this.buffer.write(b);
		super.write(b);
	}

	/**
	 * The calls to the write() methods of the internal "buffer"
	 * (ByteArrayOutputStream) precede the calls to the original stream (which,
	 * in turn, can be accessed via super, or even via this.out, since the
	 * corresponding parameter of the FilterOutputStream is protected). This
	 * makes sure that the bytes will be buffered, even if there is an exception
	 * while writing to the original stream.
	 * 
	 * To reduce the overhead, the calls to super in the above class can be
	 * omitted - e.g., if only the "conversion" to a byte array is desired. Even
	 * the ByteArrayOutputStream or OutputStream classes can be used as parent
	 * classes, with a bit more work and some assumptions (e.g., about the
	 * reset() method).
	 * 
	 * In any case, enough memory has to be available for the draining to take
	 * place and for the toByteArray() method to work.
	 */
	public byte[] toByteArray() {
		return this.buffer.toByteArray();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = new File("D:\\text.txt");
		String content = "This is the text content";

		try (OutputStream os = new FileOutputStream(file)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			DrainableOutputStream drainableOutputStream = new DrainableOutputStream(os);
			drainableOutputStream.write(contentInBytes);
			String string = new String(drainableOutputStream.toByteArray());
			System.out.println(string);
		}
	}
}