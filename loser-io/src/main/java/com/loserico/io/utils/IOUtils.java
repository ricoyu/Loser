package com.loserico.io.utils;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.lang.String.join;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.text.MessageFormat.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.loserico.io.ContentTypes;
import com.loserico.io.exception.FileCopyException;
import com.loserico.io.exception.FileDownloadException;
import com.loserico.io.exception.FileInputStreamException;

/**
 * 
 * @author Loser
 * @since Jun 1, 2016
 * @version 2.0
 *
 */
public class IOUtils {
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

	/**
	 * Represents the end-of-file (or stream).
	 */
	public static final int EOF = -1;

	/**
	 * The Unix directory separator character.
	 */
	public static final String DIR_SEPARATOR_UNIX = "/";
	/**
	 * The Windows directory separator character.
	 */
	public static final char DIR_SEPARATOR_WINDOWS = '\\';
	/**
	 * The system directory separator character.
	 */
	public static final char DIR_SEPARATOR = File.separatorChar;
	/**
	 * The Unix line separator string.
	 */
	public static final String LINE_SEPARATOR_UNIX = "\n";
	/**
	 * The Windows line separator string.
	 */
	public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

	public static final String CLASSPATH_PREFIX = "classpath*:";

	public static final Charset GBK = Charset.forName("GBK");
	public static final Charset ASCII = Charset.forName("ASCII");

	public static void closeSilently(final Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void copyFile(final File in, final File out) throws IOException {
		final RandomAccessFile f1 = new RandomAccessFile(in, "r");
		final RandomAccessFile f2 = new RandomAccessFile(out, "rw");
		try {
			final FileChannel c1 = f1.getChannel();
			final FileChannel c2 = f2.getChannel();
			try {
				c1.transferTo(0, f1.length(), c2);
				c1.close();
				c2.close();
			} catch (final IOException ex) {
				closeSilently(c1);
				closeSilently(c2);
				// Propagate the original exception
				throw ex;
			}
			f1.close();
			f2.close();
		} catch (final IOException ex) {
			closeSilently(f1);
			closeSilently(f2);
			throw ex;
		}
	}

	/**
	 * 读取文件系统中的文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		StringBuilder result = new StringBuilder();
		File file = new File(filePath);
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append(LINE_SEPARATOR_UNIX);
			}
			scanner.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return result.toString();
	}

	public static String readFile(InputStream in) {
		StringBuilder result = new StringBuilder();
		try (Scanner scanner = new Scanner(in)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append(LINE_SEPARATOR_UNIX);
			}
			scanner.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result.toString();
	}

	/**
	 * 将文件读到byte[]中
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] readFileAsBytes(String filePath) {
		Path path = Paths.get(filePath);
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			logger.error("Read file as bytes failed!", e);
		}
		return new byte[0];
	}

	public static InputStream readFileAsStream(String filePath) throws IOException {
		return Files.newInputStream(Paths.get(filePath), READ);
	}

	public static List<String> readLines(String filePath) {
		List<String> lines = new ArrayList<String>();
		File file = new File(filePath);
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
			scanner.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return lines;
	}

	/**
	 * 从InputStream读取字符串
	 * 
	 * @param in
	 * @param autoClose
	 * @return String
	 */
	public static String readAsString(InputStream in) {
		return readAsString(in, true);
	}

	/**
	 * 从InputStream读取字符串
	 * 
	 * @param in
	 * @param autoClose
	 * @return String
	 */
	public static String readAsString(InputStream in, boolean autoClose) {
		List<String> lines = new ArrayList<String>();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
				Scanner scanner = new Scanner(bufferedInputStream)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
			scanner.close();
			if (autoClose) {
				in.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return join(lines, "\n");
	}

	public static List<String> readLines(InputStream in) {
		List<String> lines = new ArrayList<String>();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
				Scanner scanner = new Scanner(bufferedInputStream)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				lines.add(line);
			}
			scanner.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return lines;
	}

	public static String readFile(Path path) {
		Objects.requireNonNull(path, "path cannot be null!");
		StringBuilder result = new StringBuilder();
		try (Scanner scanner = new Scanner(path.toFile())) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append(LINE_SEPARATOR_UNIX);
			}
			scanner.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return result.toString();
	}

	public static byte[] readFileAsBytes(Path path) {
		Objects.requireNonNull(path, "path cannot be null!");
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			logger.error("Read file as bytes failed!", e);
		}
		return new byte[0];
	}

	/**
	 * 读取classpath下文件内容,文件不存在则返回null PathMatchingResourcePatternResolver
	 * 
	 * @param fileName
	 * @return String
	 */
	public static String readClassPathFile(String fileName) {
		InputStream in = readClasspathFileAsInputStream(fileName);
		if (in == null) {
			logger.debug("Cannot file {} under classpath", fileName);
			return null;
		}
		return readFile(in);
	}

	/**
	 * 将classpath的文件读到byte[]中
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] readClassPathFileAsBytes(String fileName) {
		File file = readClasspathFileAsFile(fileName);
		if (file == null) {
			return new byte[0];
		}
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			logger.error("Read file as bytes failed!", e);
		}
		return new byte[0];
	}

	public static List<String> readLinesFromClassPath(String fileName) {
		InputStream in = readClasspathFileAsInputStream(fileName);
		return readLines(in);
	}

	/**
	 * 读取classpath下某个文件，返回File
	 * 
	 * @param fileName
	 * @return File
	 */
	public static File readClasspathFileAsFile(String fileName) {
		ClassLoader classLoader = firstNonNull(currentThread().getContextClassLoader(), IOUtils.class.getClassLoader());
		URL url = classLoader.getResource(fileName);
		if (url == null && !fileName.startsWith(DIR_SEPARATOR_UNIX)) {
			logger.warn("Cannot find file {} under classpath", fileName);
			url = classLoader.getResource("/" + fileName);
		}
		if (url != null) {
			return new File(url.getFile());
		}

		/*
		 * Java Application中不带目录的时候可以查到
		 */
		List<File> files = Resources.getResources(fileName);
		if (!files.isEmpty()) {
			return files.get(0);
		}

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(fileName);
		if (resource.exists()) {
			try {
				return resource.getFile();
			} catch (IOException e) {
				logger.error("", e);
				return null;
			}
		}

		try {
			if (!fileName.startsWith(DIR_SEPARATOR_UNIX)) {
				fileName = CLASSPATH_PREFIX + DIR_SEPARATOR_UNIX + "**" + DIR_SEPARATOR_UNIX + fileName;
			}
			Resource[] resources = resolver.getResources(fileName);
			if (resources.length > 0) {
				return resources[0].getFile();
			}
			return null;
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}

	}

	/**
	 * 读取classpath下某个文件，返回InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream readClasspathFileAsInputStream(String fileName) {
		ClassLoader classLoader = firstNonNull(currentThread().getContextClassLoader(), IOUtils.class.getClassLoader());
		URL url = classLoader.getResource(fileName);
		if (url == null && !fileName.startsWith(DIR_SEPARATOR_UNIX)) {
			logger.debug("Cannot find file {} under classpath", fileName);
			url = classLoader.getResource("/" + fileName);
		}
		if (url != null) {
			try {
				return url.openStream();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		}

		/*
		 * Java Application中不带目录的时候可以查到
		 */
		List<File> files = Resources.getResources(fileName);
		if (!files.isEmpty()) {
			try {
				return Files.newInputStream(files.get(0).toPath(), NOFOLLOW_LINKS);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		}

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(fileName);
		if (resource.exists()) {
			try {
				return resource.getInputStream();
			} catch (IOException e) {
				logger.error("", e);
				return null;
			}
		}

		try {
			if (!fileName.startsWith(DIR_SEPARATOR_UNIX)) {
				fileName = CLASSPATH_PREFIX + DIR_SEPARATOR_UNIX + "**" + DIR_SEPARATOR_UNIX + fileName;
			}
			Resource[] resources = resolver.getResources(fileName);
			if (resources.length > 0) {
				return resources[0].getInputStream();
			}
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}

		return null;
	}

	/**
	 * Write string data to file
	 * 
	 * @param filePath
	 * @param data
	 * @return
	 */
	public static boolean write(String filePath, String data) {
		Objects.requireNonNull(filePath, "filePath cannot be null!");
		Path path = Paths.get(filePath);
		return write(path, data);
	}

	public static boolean write(String filePath, String data, Charset charset) {
		Objects.requireNonNull(filePath, "filePath cannot be null!");
		Path path = Paths.get(filePath);
		return write(path, data, charset);
	}

	public static boolean write(String filePath, byte[] data) {
		Objects.requireNonNull(filePath, "filePath cannot be null!");
		Path path = Paths.get(filePath);
		return write(path, data);
	}

	/**
	 * Write string data to file
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public static boolean write(Path path, String data) {
		Objects.requireNonNull(path, "path cannot be null!");
		return write(path, Optional.of(data).orElse("").getBytes(UTF_8), CREATE, APPEND);
	}

	public static boolean write(Path path, byte[] data) {
		Objects.requireNonNull(path, "path cannot be null!");
		return write(path, data, CREATE, APPEND);
	}

	/**
	 * 用指定的编码格式写文件
	 * 
	 * @param path
	 * @param data
	 * @param charset
	 * @return
	 */
	public static boolean write(Path path, String data, Charset charset) {
		Objects.requireNonNull(path, "path cannot be null!");
		return write(path, Optional.of(data).orElse("").getBytes(charset), CREATE, APPEND);
	}

	/**
	 * Write byte[] data to file
	 * 
	 * @param path
	 * @param data
	 * @param options
	 * @return
	 */
	public static boolean write(Path path, byte[] data, OpenOption... options) {
		Objects.requireNonNull(path, "path cannot be null!");
		createParentDir(path);
		try {
			Files.write(path, data, options);
			return true;
		} catch (IOException e) {
			logger.error(format("Write data [{0}] to path [{1}] failed!", data, path), e);
		}
		return false;
	}

	/**
	 * 将content写入临时文件
	 * 
	 * @param fileName
	 * @param suffix
	 * @param content
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static Path writeTempFile(String fileName, String suffix, String content, Charset charset) throws IOException {
		Path path = tempFile(fileName, suffix).toPath();
		write(path, content, charset);
		return path;
	}

	/**
	 * @of
	 * Create parent directory if this path has a parent path and if not exist yet
	 * 不管是否执行了父目录的创建，返回父目录存在与否的最终状态
	 * true 存在
	 * false 不存在
	 * 
	 * @param path
	 * @return boolean 
	 * @on
	 */
	public static boolean createParentDir(Path path) {
		Optional.of(path.getParent())
				.ifPresent(parent -> {
					if (!Files.exists(parent, NOFOLLOW_LINKS)) {
						try {
							Files.createDirectories(parent);
						} catch (IOException e) {
							logger.error(format("create parent directory [{0}] failed", parent), e);
						}
					}
				});
		return Files.exists(path.getParent(), NOFOLLOW_LINKS);
	}

	public static boolean createDir(Path path) {
		Optional.of(path)
				.ifPresent(dir -> {
					if (!Files.exists(dir, NOFOLLOW_LINKS)) {
						try {
							Files.createDirectories(dir);
						} catch (IOException e) {
							logger.error(format("create directory [{0}] failed", dir), e);
						}
					}
				});
		return Files.exists(path, NOFOLLOW_LINKS);
	}

	/**
	 * Delete a file if exists
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(Path path) {
		Objects.requireNonNull(path, "path cannot be null!");
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			logger.error(format("Delete file {0} failed", path), e);
		}
		return true;
	}

	/**
	 * Delete specified directory with its sub-dir and all of tis files
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory(String path) {
		Path directory = Paths.get(path);
		return deleteDirectory(directory);
	}

	public static boolean deleteDirectory(File path) {
		Path directory = path.toPath();
		return deleteDirectory(directory);
	}

	/**
	 * Delete specified directory with its sub-dir
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteDirectory(Path path) {
		if (!Files.isDirectory(path, NOFOLLOW_LINKS)) {
			logger.error("{} is not a directory!", path);
			return false;
		}
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
			return true;
		} catch (IOException e) {
			logger.error("Delete path " + path + " failed", e);
			return false;
		}
	}

	/**
	 * 将source文件移动到targetFolder
	 * 
	 * @param source
	 * @param targetFolder
	 * @return
	 */
	public static void move(Path source, Path targetFolder) throws IOException {
		move(source, targetFolder, null);
	}

	/**
	 * 将source文件移动到targetFolder,并重命名为renameTo
	 * 
	 * @param source
	 * @param targetFolder
	 * @param renameTo
	 * @return
	 */
	public static void move(Path source, Path targetFolder, String renameTo) throws IOException {
		if (Files.notExists(source, NOFOLLOW_LINKS)) {
			return;
		}

		if (Files.notExists(targetFolder, NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(targetFolder);
			} catch (Throwable e) {
				String msg = format("Create directory[{0}] failed", targetFolder.toString());
				logger.error(msg, e);
				throw new IOException(msg, e);
			}
		}
		try {
			Path targetFile = null;
			if (isNotBlank(renameTo)) {
				targetFile = targetFolder.resolve(renameTo);
			} else {
				targetFile = targetFolder.resolve(source.getFileName());
			}
			Files.move(source, targetFile, REPLACE_EXISTING);
		} catch (Throwable e) {
			String msg = format("Move file[{0}] to [{1}] failed.", source, targetFolder);
			logger.error(msg, e);
			throw new IOException(msg, e);
		}
	}

	/**
	 * 获取根目录
	 * 
	 * @param path
	 * @return
	 */
	public static Path getRootDirectory(Path path) {
		return Optional.ofNullable(path.getRoot())
				.map(root -> {
					StringBuilder pathStr = new StringBuilder();
					pathStr.append(root.toString());
					Optional.of(path.subpath(0, 1)).ifPresent(sub -> pathStr.append(sub));
					return Paths.get(pathStr.toString());
				}).orElseGet(() -> path.subpath(0, 1));
	}

	/**
	 * 将数据从 InputStream 拷贝到 OutputStream，最后两个都关闭
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyAndClose(final InputStream in, final OutputStream out) throws IOException {
		try {
			copy(in, out);
			in.close();
			out.close();
		} catch (IOException ex) {
			closeSilently(in);
			closeSilently(out);
			throw ex;
		}
	}

	/**
	 * 将数据从 InputStream 拷贝到 OutputStream，只关闭输入流
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyAndCloseFirst(final InputStream in, final OutputStream out) throws IOException {
		try {
			copy(in, out);
			in.close();
		} catch (IOException ex) {
			closeSilently(in);
			throw ex;
		}
	}

	/**
	 * 将path代表的文件写入OutputStream
	 * 
	 * @param path
	 * @param out
	 * @throws IOException
	 */
	public static void copy(Path path, final OutputStream out) throws IOException {
		InputStream inputStream = Files.newInputStream(path, READ);
		final byte[] buf = new byte[2048];
		int len;
		while ((len = inputStream.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		inputStream.close();
	}

	public static void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buf = new byte[2048];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
	}

	/**
	 * 通过NIO方式拷贝数据，每读取一部分数据就立刻写入输出流
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyPositive(InputStream in, OutputStream out) throws IOException {
		ReadableByteChannel inChannel = Channels.newChannel(in);
		WritableByteChannel outChannel = Channels.newChannel(out);

		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int read;

		while ((read = inChannel.read(buffer)) > 0) {
			buffer.rewind();
			buffer.limit(read);

			while (read > 0) {
				read -= outChannel.write(buffer);
			}

			buffer.clear();
		}
	}

	public static void copyNegative(InputStream in, OutputStream out) throws IOException {
		ReadableByteChannel inChannel = Channels.newChannel(in);
		WritableByteChannel outChannel = Channels.newChannel(out);

		ByteBuffer buffer = ByteBuffer.allocate(8192);
		while (inChannel.read(buffer) != -1) {
			buffer.flip();
			outChannel.write(buffer);
			buffer.compact();
		}

		buffer.flip();
		while (buffer.hasRemaining()) {
			outChannel.write(buffer);
		}
		inChannel.close();
		outChannel.close();
	}

	/*
	 * Copy one file to another place
	 */
	public static boolean copy(Path copyFrom, Path copyTo, CopyOption... options) {
		boolean parentCreateResult = createParentDir(copyTo);
		if (parentCreateResult) {
			try (InputStream is = new FileInputStream(copyFrom.toFile())) {
				Files.copy(is, copyTo, options);
				return true;
			} catch (IOException e) {
				logger.error(format("copy from [{0}] to [{1}] failed!", copyFrom, copyTo), e);
			}
		}
		return false;
	}

	/**
	 * 保留文件的后缀，将文件名前缀替换为随机字符串+日期
	 * 
	 * @param fileName
	 * @return String 随机生成的文件名
	 */
	public static String randomFileName(String fileName) {
		if (isBlank(fileName)) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf(".");
		String suffix = dotIndex == -1 ? "" : fileName.substring(dotIndex);
		String baseName = RandomStringUtils.randomAlphanumeric(16);
		String timeSuffix = LocalDateTime.now().format(ofPattern("yyyyMMddHHmmss"));
		return join("", baseName, timeSuffix, suffix);
	}

	public static BufferedReader toBufferedReader(InputStream in) {
		return new BufferedReader(new InputStreamReader(in, UTF_8));
	}

	public static BufferedReader toBufferedReader(InputStream in, Charset charset) {
		return new BufferedReader(new InputStreamReader(in, charset));
	}

	public static File toFile(InputStream in) throws IOException {
		final File tempFile = File.createTempFile(RandomStringUtils.randomAlphanumeric(16), "tmp");
		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		return tempFile;
	}

	public static byte[] toBytes(String path) throws IOException {
		requireNonNull(path, "path 不能为null");
		return Files.readAllBytes(Paths.get(path));
	}

	public static byte[] toBytes(File file) throws IOException {
		requireNonNull(file, "file 不能为null");
		return Files.readAllBytes(file.toPath());
	}
	
	public static byte[] toBytes(Path path) throws IOException {
		requireNonNull(path, "path 不能为null");
		return Files.readAllBytes(path);
	}

	/**
	 * 获取文件大小 如果path代表一个目录，获取目录中所有文件大小之和
	 * 
	 * @param path
	 * @return
	 */
	public static long length(Path path) {
		return FileUtils.sizeOf(path.toFile());
	}

	/**
	 * Path转InputStream
	 * 
	 * @param path
	 * @return InputStream
	 */
	public static InputStream toInputStream(Path path) {
		try {
			return Files.newInputStream(path, READ);
		} catch (IOException e) {
			throw new FileInputStreamException(e);
		}
	}

	/**
	 * Path转InputStream
	 * 
	 * @param path
	 * @param options
	 * @return
	 */
	public static InputStream toInputStream(Path path, OpenOption... options) {
		try {
			return Files.newInputStream(path, options);
		} catch (IOException e) {
			throw new FileInputStreamException(e);
		}
	}

	public static ByteArrayInputStream toByteArrayInputStream(String path) throws IOException {
		return new ByteArrayInputStream(Files.readAllBytes(Paths.get(path)));
	}

	public static ByteArrayInputStream toByteArrayInputStream(File file) throws IOException {
		return new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
	}

	/**
	 * 在临时目录创建指定后缀的文件
	 * 
	 * @param suffix
	 * @return
	 * @throws IOException
	 */
	public static File tempFile(String suffix) throws IOException {
		if (suffix != null && suffix.indexOf(".") != 0) {
			suffix = "." + suffix;
		}
		return File.createTempFile(RandomStringUtils.randomAlphanumeric(16), suffix);
	}

	/**
	 * 在临时目录创建指定文件名和后缀的文件 java.io.tmpdir
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 * @throws IOException
	 */
	public static File tempFile(String fileName, String suffix) throws IOException {
		Objects.requireNonNull(fileName, "fileName 不可以为null哦");
		if (suffix != null && suffix.indexOf(".") != 0) {
			suffix = "." + suffix;
		}
		String tempDir = System.getProperty("java.io.tmpdir");
		return Paths.get(tempDir, fileName + suffix).toFile();
	}

	public static Path fileCopy(Path sourcePath) {
		Objects.requireNonNull(sourcePath);
		String fileName = sourcePath.getFileName().toString();
		String suffix = FilenameUtils.getExtension(fileName);
		try {
			Path destPath = tempFile(suffix).toPath();
			Files.copy(sourcePath, destPath, REPLACE_EXISTING);
			return destPath;
		} catch (IOException e) {
			logger.error("Failed to copy file {} ", fileName);
			throw new FileCopyException(e);
		}
	}
	
	public static Path fileCopy(String sourceFileName) {
		Objects.requireNonNull(sourceFileName);
		Path sourcePath = Paths.get(sourceFileName);
		return fileCopy(sourcePath);
	}

	/**
	 * 
	 * @param sourceFileName
	 * @param targetFileName
	 *            不带后缀哦
	 * @return
	 */
	public static Path fileCopy(String sourceFileName, String targetFileName) {
		Objects.requireNonNull(sourceFileName);
		Path sourcePath = Paths.get(sourceFileName);
		String suffix = FilenameUtils.getExtension(sourceFileName);
		try {
			Path destPath = tempFile(targetFileName, suffix).toPath();
			Files.copy(sourcePath, destPath, REPLACE_EXISTING);
			return destPath;
		} catch (IOException e) {
			logger.error("Failed to copy file {} ", sourceFileName);
			throw new FileCopyException(e);
		}
	}

	/**
	 * 返回文件名后缀 <pre> foo.txt --&gt; "txt" a/b/c.jpg --&gt; "jpg" a/b.txt/c
	 * --&gt; "" a/b/c --&gt; "" </pre>
	 * 
	 * @param filename
	 * @return
	 */
	public static String fileExtension(String filename) {
		return FilenameUtils.getExtension(filename);
	}

	/**
	 * 压缩单个文件或者整个文件夹
	 * 
	 * @param path
	 * @return Path
	 * @throws IOException
	 */
	public static Path zipFile(File fileToZip) throws IOException {
		if (fileToZip.isHidden()) {
			return null;
		}

		String zippedFileName = fileToZip.getName() + ".zip";
		Path zippedFilePath = Paths.get(zippedFileName);
		FileOutputStream fos = new FileOutputStream(zippedFilePath.toFile());
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileToZip.getName() + "/" + childFile.getName(), zipOut);
			}
		}

		return zippedFilePath;
	}

	public static void main(String[] args) throws IOException {
		File tempFile = tempFile("123456", ".xlsx");
		System.out.println(tempFile);
	}

	/**
	 * 压缩文件 fileToZip，最终的压缩文件为 destZippedFile
	 * 
	 * @param fileToZip
	 *            要压缩的文件或目录
	 * @param destZippedFile
	 *            压缩生成的zip文件
	 * @return File
	 * @throws IOException
	 */
	public static File zipFile(String fileToZip, String destZippedFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(destZippedFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File file = new File(fileToZip);
		zipFile(file, file.getName(), zipOut);
		return Paths.get(destZippedFile).toFile();
	}

	public static void zipFiles(List<File> srcFiles, File destFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(destFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		for (File srcFile : srcFiles) {
			FileInputStream fis = new FileInputStream(srcFile);
			ZipEntry zipEntry = new ZipEntry(srcFile.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) >= 0) {
				zipOut.write(buffer, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();
	}

	/**
	 * 压缩文件 fileToZip，最终的压缩文件为 destZippedFile
	 * 
	 * @param fileToZip
	 *            要压缩的文件或目录
	 * @param destZippedFile
	 *            压缩生成的zip文件
	 * @return File
	 * @throws IOException
	 */
	public static File zipFile(File fileToZip, File destZippedFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(destZippedFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		zipFile(fileToZip, fileToZip.getName(), zipOut);
		return destZippedFile;
	}

	/**
	 * 压缩文件或者目录
	 * 
	 * @param fileToZip
	 * @param fileName
	 * @param zipOut
	 * @throws IOException
	 */
	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}

		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}

		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	/**
	 * 文件下载， Content-Type 默认为 application/octet-stream， 下载文件名也是默认的
	 * 
	 * @param File
	 * @param response
	 */
	public static void download(File file, HttpServletResponse response) {
		requireNonNull(file, "file cannot be null");
		download(file.toPath(), response);
	}

	/**
	 * 文件下载， Content-Type 默认为 application/octet-stream， 下载文件名也是默认的
	 * 
	 * @param path
	 * @param response
	 */
	public static void download(Path path, HttpServletResponse response) {
		requireNonNull(path, "path cannot be null");
		response.setContentType(ContentTypes.OCTET.getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=" + path.getFileName());
		try {
			copy(path, response.getOutputStream());
		} catch (IOException e) {
			logger.error("下载文件出错", e);
			throw new FileDownloadException(path, e);
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param path
	 * @param response
	 */
	public static void download(Path path, HttpServletResponse response, String contentType, String fileName) {
		requireNonNull(path, "path cannot be null");
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		try {
			copy(path, response.getOutputStream());
		} catch (IOException e) {
			logger.error("下载文件出错", e);
			throw new FileDownloadException(path, e);
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param data
	 * @param response
	 * @param contentType
	 * @param fileName 文件名及后缀
	 */
	public static void download(byte[] data, HttpServletResponse response, String contentType, String fileName) {
		requireNonNull(data, "data cannot be null");
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		try {
			response.getOutputStream().write(data);
		} catch (IOException e) {
			logger.error("下载文件出错", e);
			throw new FileDownloadException(e);
		}
	}
	
	/**
	 * 文件下载
	 * 
	 * @param data
	 * @param response
	 * @param contentType
	 * @param fileName 文件名及后缀
	 */
	public static void downloadInline(byte[] data, HttpServletResponse response, String contentType, String fileName) {
		requireNonNull(data, "data cannot be null");
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "inline; filename=" + fileName);
		try {
			response.getOutputStream().write(data);
		} catch (IOException e) {
			logger.error("下载文件出错", e);
			throw new FileDownloadException(e);
		}
	}
	
	/**
	 * 文件下载
	 * 
	 * @param data
	 * @param response
	 * @param contentType
	 * @param fileName 文件名及后缀
	 */
	public static void download(ByteArrayOutputStream outputStream, HttpServletResponse response, String contentType, String fileName) {
		requireNonNull(outputStream, "outputStream cannot be null");
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		try {
			response.getOutputStream().write(outputStream.toByteArray());
		} catch (IOException e) {
			logger.error("下载文件出错", e);
			throw new FileDownloadException(e);
		}
	}
	
	/**
	 * 文件下载
	 * 
	 * @param data
	 * @param response
	 * @param fileName	文件名
	 * @param extension	文件后缀, 可以.号开头也不可不含.号
	 */
	public static void smartDownload(byte[] data, HttpServletResponse response, String fileName, String extension) {
		requireNonNull(extension, "文件后缀不能为null");
		if (extension.indexOf(".") != 0) {
			extension = "." + extension;
		}
		String contentType = ContentTypes.contentType(extension);
		fileName = fileName + extension;
		download(data, response, contentType, fileName);
	}
	
	/**
	 * 文件下载
	 * 
	 * @param outputStream
	 * @param response
	 * @param fileName	文件名
	 * @param extension	文件后缀, 可以.号开头也不可不含.号
	 */
	public static void smartDownload(ByteArrayOutputStream outputStream, HttpServletResponse response, String fileName, String extension) {
		requireNonNull(extension, "文件后缀不能为null");
		if (extension.indexOf(".") != 0) {
			extension = "." + extension;
		}
		String contentType = ContentTypes.contentType(extension);
		fileName = fileName + extension;
		download(outputStream.toByteArray(), response, contentType, fileName);
	}
}