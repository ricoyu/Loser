package com.loserico.io;

import static com.loserico.orm.utils.QueryUtils.enumName;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.Test;

/**
 * Applications often interact with a file system, which is usually implemented as a hierarchy of files and directories
 * starting from a root directory. Operating systems on which a Java virtual machine (JVM) runs typically support at
 * least one file system. For example, Unix/Linux combines all mounted (attached and prepared) disks into one virtual
 * file system. In contrast, Windows associates a separate file system with each active disk drive. Java offers access
 * to the underlying operating system’s available file system(s) via its concrete java.io.File class,
 * 
 * 
 * 
 * @author Rico Yu
 * @since 2016-11-27 12:46
 * @version 1.0
 *
 */
public class FileTest {

	/**
	 * The example constructs a File object initialized to the file system object temp. It then calls mkdir() on this
	 * File object to make a new directory named temp.
	 */
	@Test
	public void testMkDir() {
		new File("temp").mkdir(); // 在项目根目录下创建temp目录
	}

	/**
	 * The java.io package’s classes default to resolving relative paths against the current user (also known as
	 * working) directory, which is identified by the system property user.dir and which is typically the directory in
	 * which the JVM was launched. (You obtain a system property value by calling the java.lang.System class’s
	 * getProperty() method.)
	 * 
	 * 所以上面例子中创建的temp目录在项目的根目录下，因为在Eclipse中运行JUnit中运行单元测试的current user (also known as working) directory就是项目根目录
	 */
	@Test
	public void testSystemProperty() {
		System.out.println(System.getProperty("user.dir")); // D:\Loser\loser-io
		System.out.println(System.getProperty("java.io.tmpdir"));// C:\Users\Loser\AppData\Local\Temp\
		System.out.println(System.getProperty("file.encoding"));//The default character encoding
		System.out.print(System.getProperty("line.separator"));//换行符
		System.out.println("---------");
	}

	/**
	 * The default name-separator character is defined by the system property file.separator and is made available in
	 * File’s public static separator and separatorChar fields—the first field stores the character in a
	 * java.lang.String instance and the second field stores it as a char value.
	 */
	@Test
	public void testSeparator() {
		System.out.println(System.getProperty("file.separator"));
		System.out.println(File.separator);
		System.out.println(File.separatorChar);
		System.out.println(File.pathSeparator);
		System.out.println(File.pathSeparatorChar);
	}

	/**
	 * Because File(String path), File(String parent, String child), and File(File parent, String child) don’t detect
	 * invalid path arguments (apart from throwing a java.lang.NullPointerException when path or child is null), you
	 * must be careful when specifying paths. You should strive to only specify paths that are valid for all operating
	 * systems on which the application will run. For example, instead of hard-coding a drive specifier (such as C:) in
	 * a path, use a root returned from listRoots(), which I discuss later. Even better, keep your paths relative to the
	 * current user/working directory (returned from the user.dir system property).
	 */
	@Test
	public void testFileConstructor() {
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			File file = roots[i];
			// C:\
			// D:\
			// E:\
			System.out.println(file.getAbsolutePath());
		}
	}

	@Test
	public void testPathInfo() {
		String[] paths = new String[] { "loser.png", ".", "", "D:\\Loser\\loser-io\\target\\..\\loser.png" };
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			System.out.println("\n========= For Path:[\"" + path + "\"]");
			File file = new File(path);
			System.out.println("Absolute path = " + file.getAbsolutePath());
			try {
				System.out.println("Canonical path = " + file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Name = " + file.getName());
			System.out.println("Parent = " + file.getParent());
			System.out.println("Path = " + file.getPath());
			System.out.println("Is absolute = " + file.isAbsolute());
		}
	}

	@Test
	public void testFileDirectoryInfo() {
		String[] paths = new String[] { "loser.png", ".", "", "D:\\Loser\\loser-io\\target\\..\\loser.png" };
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			System.out.println("\n========= For Path:[\"" + path + "\"]");
			File file = new File(path);
			System.out.println("About " + file + ":");
			System.out.println("Exists = " + file.exists());
			System.out.println("Is directory = " + file.isDirectory());
			System.out.println("Is file = " + file.isFile());
			System.out.println("Is hidden = " + file.isHidden());
			System.out.println("Last modified = " + new Date(file.lastModified()));
			System.out.println("Length = " + file.length());
		}
	}

	/**
	 * long getFreeSpace() returns the number of unallocated bytes in the partition identified by this File object’s
	 * abstract path; it returns zero when the abstract path doesn’t name a partition.
	 * 
	 * long getUsableSpace() returns the number of bytes available to the current JVM on the partition identified by
	 * this File object’s abstract path; it returns zero when the abstract path doesn’t name a partition.
	 * 
	 * long getTotalSpace() returns the size (in bytes) of the partition identified by this File object’s abstract path;
	 * it returns zero when the abstract path doesn’t name a partition.
	 * 
	 * Although getFreeSpace() and getUsableSpace() appear to be equivalent, they differ in the following respect:
	 * unlike getFreeSpace(), getUsableSpace() checks for write permissions and other operating system restrictions,
	 * resulting in a more accurate estimate.
	 * 
	 * The getFreeSpace() and getUsableSpace() methods return a hint (not a guarantee) that a Java application can use
	 * all (or most) of the unallocated or available bytes. These values are hints because a program running outside the
	 * JVM can allocate partition space, resulting in actual unallocated and available values being lower than the
	 * values returned by these methods.
	 */
	@Test
	public void testPartitionSpace() {
		File[] roots = File.listRoots();
		for (File root : roots) {
			System.out.println("Partition: " + root);
			System.out.println("Free space on this partition = " + root.getFreeSpace() +
					"Byte, " + root.getFreeSpace() / 1024 / 1024 +
					"M, " + root.getFreeSpace() / 1024 / 1024 / 1024 + "G");
			System.out.println("Usable space on this partition = " + root.getUsableSpace() +
					"Byte, " + root.getUsableSpace() / 1024 / 1024 +
					"M, " + root.getUsableSpace() / 1024 / 1024 / 1024 + "G");
			System.out.println("Total space on this partition = " + root.getTotalSpace() +
					"Byte, " + root.getTotalSpace() / 1024 / 1024 +
					"M, " + root.getTotalSpace() / 1024 / 1024 / 1024 + "G");
			System.out.println("***");
		}
	}

	/**
	 * <pre>String[] list()</pre> Return a potentially empty array of strings naming the files and directories in the
	 * directory denoted by this File object’s abstract path. If the path doesn’t denote a directory, or if an I/O error
	 * occurs, this method returns null. Otherwise, it returns an array of strings, one string for each file or
	 * directory in the directory. Names denoting the directory itself and the directory’s parent directory are not
	 * included in the result. Each string is a file name rather than a complete path. Also, there is no guarantee that
	 * the name strings in the resulting array will appear in alphabetical or any other order.
	 * 
	 * <pre>String[] list(FilenameFilter filter)</pre> A convenience method for calling list() and returning only those
	 * Strings that satisfy filter.
	 * 
	 * <pre>File[] listFiles()</pre> A convenience method for calling list(), converting its array of Strings to an
	 * array of Files, and returning the Files array.
	 * 
	 * The <code>FilenameFilter</code> interface declares a single <pre>boolean accept(File dir, String name)</pre>
	 * method that is called for each file/directory located in the directory identified by the File object’s path:
	 * 
	 * dir identifies the parent portion of the path (the directory path).
	 * 
	 * name identifies the final directory name or the file name portion of the path.
	 * 
	 * The <code>java.io.FileFilter</code> interface declares a single boolean accept(String path) method that is called
	 * for each file/directory located in the directory identified by the File object’s path. The argument passed to
	 * path identifies the complete path of the file or directory.
	 * 
	 * Because each interface’s <code>accept()</code> method accomplishes the same task, you might be wondering which
	 * interface to use. If you prefer a path broken into its directory and name components, use
	 * <code>FilenameFilter</code>. However, if you prefer a complete path, use <code>FileFilter</code>; you can always
	 * call <code>getParent()</code> and <code>getName()</code> to get these components.
	 */
	@Test
	public void testDir() {
		File file = new File("C:\\Windows\\System32");
		FilenameFilter filenameFilter = (dir, name) -> name.endsWith("exe");
		String[] names = file.list(filenameFilter);
		for (String name : names) {
			System.out.println(name);
		}
	}

	/**
	 * <pre>boolean createNewFile()</pre> Atomically create a new, empty file named by this File object’s abstract path
	 * if and only if a file with this name doesn’t yet exist. The check for file existence and the creation of the file
	 * when it doesn’t exist are a single operation that’s atomic with respect to all other file system activities that
	 * might affect the file. This method returns true when the named file doesn’t exist and was successfully created,
	 * and returns false when the named file already exists. It throws IOException when an I/O error occurs.
	 * 
	 * <pre>static File createTempFile(String prefix, String suffix)</pre> Create an empty file in the default temporary
	 * file directory using the given prefix and suffix to generate its name. This overloaded class method calls its
	 * three-parameter variant, passing prefix, suffix, and null to this other method, and returning the other method’s
	 * return value.
	 * 
	 * <pre>static File createTempFile(String prefix, String suffix, File directory)</pre> Create an empty file in the
	 * specified directory using the given prefix and suffix to generate its name. The name begins with the character
	 * sequence specified by prefix and ends with the character sequence specified by suffix; “.tmp” is used as the
	 * suffix when suffix is null. This method returns the created file’s path when successful. It throws
	 * java.lang.IllegalArgumentException when prefix contains fewer than three characters and IOException when the file
	 * can’t be created.
	 * 
	 * <pre>boolean delete()</pre> Delete the file or directory denoted by this File object’s path. Return true when
	 * successful; otherwise, return false. If the path denotes a directory, the directory must be empty in order to be
	 * deleted.
	 * 
	 * <pre>void deleteOnExit()</pre> Request that the file or directory denoted by this File object’s abstract path be
	 * deleted when the JVM terminates. Reinvoking this method on the same File object has no effect. Once deletion has
	 * been requested, it’s not possible to cancel the request. Therefore, this method should be used with care.
	 * 
	 * <pre>boolean mkdir()</pre> Create the directory named by this File object’s abstract path. Return true when
	 * successful; otherwise, return false.
	 * 
	 * <pre>boolean mkdirs()</pre> Create the directory and any necessary intermediate directories named by this File
	 * object’s abstract path. Return true when successful; otherwise, return false.
	 * 
	 * <pre>boolean renameTo(File dest)</pre> Rename the file denoted by this File object’s abstract path to dest.
	 * Return true when successful; otherwise, return false. This method throws NullPointerException when dest is null.
	 * Many aspects of this method’s behavior are operating system-dependent. For example, the rename operation might
	 * not be able to move a file from one file system to another, the operation might not be atomic, or it might not
	 * succeed when a file with the destination path already exists. The return value should always be checked to make
	 * sure that the rename operation was successful.
	 * 
	 * <pre>boolean setLastModified(long time)</pre> Set the last-modified time of the file or directory named by this
	 * File object’s abstract path. Return true when successful; otherwise, return false. This method throws
	 * IllegalArgumentException when time is negative. All operating systems support file-modification times to the
	 * nearest second, but some provide more precision. The time value will be truncated to fit the supported precision.
	 * If the operation succeeds and no intervening operations on the file take place, the next call to lastModified()
	 * will return the (possibly truncated) time value passed to this method.
	 */
	@Test
	public void testTempFileDemo() {
		System.out.println(System.getProperty("java.io.tmpdir"));
		File temp;
		try {
			// C:\Users\Loser\AppData\Local\Temp\text2705793947307707346.txt
			temp = File.createTempFile("text", ".txt");
			System.out.println(temp);
			/*
			 * After outputting the location where temporary files are stored, testTempFileDemo creates a temporary file
			 * whose name begins with text and which ends with the .txt extension. TempFileDemo next outputs the
			 * temporary file’s name and registers the temporary file for deletion upon the successful termination of
			 * the application.
			 */
			temp.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @formatter:off
	 * <pre>setExecutable(boolean executable)</pre> 
	 * <pre>boolean setExecutable(boolean executable, boolean ownerOnly)</pre> 
	 * <pre>setReadable(boolean readable)</pre> 
	 * <pre>boolean setReadable(boolean readable, boolean ownerOnly)</pre> 
	 * <pre>boolean setWritable(boolean writable)</pre> 
	 * <pre>boolean setWritable(boolean writable, boolean ownerOnly)</pre>
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPermissions() throws IOException {
		File temp = File.createTempFile("permisstion", null);
		System.out.println(temp);
		System.out.println("Execute = " + temp.canExecute());
		System.out.println("Read = " + temp.canRead());
		System.out.println(" Write = " + temp.canWrite());
		temp.setExecutable(false, true);
		temp.setWritable(false);
		temp.setReadable(false);
		System.out.println("Execute = " + temp.canExecute());
		System.out.println(" Write = " + temp.canWrite());
		System.out.println("Read = " + temp.canRead());
		File reGetFile = new File(temp.getCanonicalPath());
		System.out.println("Execute = " + reGetFile.canExecute());
		temp.deleteOnExit();
	}
	
	@Test
	public void testProperties() {
		System.out.println(System.getProperty("user.dir"));
		System.out.println(System.getProperty("user.home"));
	}
}
