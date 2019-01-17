package com.loserico.io;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

/**
 * @formatter:off
 * <pre>RandomAccessFile(File file, String mode)</pre> 
 * <pre>RandomAccessFile(String path, String mode)</pre>
 * 
 * Create and open a new file when it doesn’t exist or open an existing file. The file is
 * identified by file’s abstract path and is created and/or opened according to mode.
 * 
 * Either constructor’s mode argument must be one of "r", "rw", "rws", or "rwd"; otherwise, the
 * constructor throws java.lang. IllegalArgumentException. These string literals have the
 * following meanings:
 * 
 * "r" informs the constructor to open an existing file for reading only. Any attempt to write
 * to the file results in a thrown instance of the java.io.IOException class.
 * 
 * "rw" informs the constructor to create and open a new file when it doesn’t exist for reading
 * and writing or open an existing file for reading and writing.
 * 
 * "rwd" informs the constructor to create and open a new file when it doesn’t exist for reading
 * and writing or open an existing file for reading and writing. Furthermore, each update to the
 * file’s content must be written synchronously to the underlying storage device.
 * 
 * "rws" informs the constructor to create and open a new file when it doesn’t exist for reading
 * and writing or open an existing file for reading and writing. Furthermore, each update to the
 * file’s content or metadata must be written synchronously to the underlying storage device.
 * 
 * A file’s metadata is data about the file and not the actual file contents. Examples of
 * metadata include the file’s length and the time the file was last modified.
 * 
 * The "rwd" and "rws" modes ensure than any writes to a file located on a local storage device
 * are written to the device, which guarantees that critical data isn’t lost when the operating
 * system crashes. No guarantee is made when the file doesn’t reside on a local device.
 * 
 * Operations on a random access file opened in "rwd" or "rws" mode are slower than these same
 * operations on a random access file opened in "rw" mode.
 * 
 * These constructors throw java.io.FileNotFoundException when mode is "r" and the file
 * identified by path cannot be opened (it might not exist or it might be a directory) or when
 * mode is "rw" and path is read-only or a directory.
 * 
 * A random access file is associated with a file pointer, a cursor that identifies the location
 * of the next byte to write or read. When an existing file is opened, the file pointer is set
 * to its first byte at offset 0. The file pointer is also set to 0 when the file is created.
 * 
 * A random access file is associated with a file pointer, a cursor that identifies the location
 * of the next byte to write or read. When an existing file is opened, the file pointer is set
 * to its first byte at offset 0. The file pointer is also set to 0 when the file is created.
 * Write or read operations start at the file pointer and advance it past the number of bytes
 * written or read. Operations that write past the current end of the file cause the file to be
 * extended. These operations continue until the file is closed.
 * 
 * @author Rico Yu
 * @since 2016-11-28 09:37
 * @version 1.0
 *
 */
public class RandomAccessFileTest {

	@Test
	public void testRandomAccessFile() throws FileNotFoundException, IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile("employee.dat", "rw");
		FileDescriptor fd = randomAccessFile.getFD();

		// Perform a critical write operation.
		randomAccessFile.write("Hello".getBytes(UTF_8));
		// Synchronize with the underlying disk by flushing the operating system output buffers
		// to the disk.
		fd.sync();
		// Perform a non-critical write operation where synchronization isn't necessary.
		randomAccessFile.write(" World!".getBytes(UTF_8));
		// Close the file, emptying output buffers to the disk.
		randomAccessFile.close();
	}
}
