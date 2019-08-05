package com.loserico.bigdata.ch03;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * 通过FileSystem API读取数据
 * 运行: export HADOOP_CLASSPATH=loser-bigdata-4.6.1.jar
 *      hadoop com.loserico.bigdata.ch03.FileSystemCat hdfs://192.168.10.110:9000/user/tom/quangle.txt
 * <p>
 * Copyright: Copyright (c) 2019-08-03 15:43
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class FileSystemCat {

	public static void main(String[] args) throws IOException {
		String uri = args[0];
		Configuration configuration = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), configuration);
		InputStream in = null;
		
		try {
			in = fs.open(new Path(uri));
			IOUtils.copyBytes(in, System.out, 4096, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}
}
