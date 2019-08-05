package com.loserico.bigdata.ch03;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * 将一个文件输出到标准输出两次
 * 运行: export HADOOP_CLASSPATH=loser-bigdata-4.6.1.jar
 *      hadoop com.loserico.bigdata.ch03.FileSystemDoubleCat hdfs://localhost:9000/user/tom/quangle.txt
 *      hadoop com.loserico.bigdata.ch03.FileSystemDoubleCat /user/tom/quangle.txt
 * <p>
 * Copyright: Copyright (c) 2019-08-03 16:00
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class FileSystemDoubleCat {

	public static void main(String[] args) throws IOException {
		String uri = args[0];
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		FSDataInputStream in = null;

		try {
			in = fs.open(new Path(uri));
			IOUtils.copyBytes(in, System.out, 4096, false);
			in.seek(0);// go back to the start of the file
			IOUtils.copyBytes(in, System.out, 4096, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}
}
