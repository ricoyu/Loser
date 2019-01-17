package com.loserico.search.lucene.course.lession1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.loserico.search.lucene.course.Constants;

public class Indexer {

	private IndexWriter writer; //写索引实例

	public Indexer(String indexDir) throws Exception {
		Directory directory = FSDirectory.open(Paths.get(indexDir));
		Analyzer analyzer = new StandardAnalyzer(); //标准分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(directory, config);
	}

	/**
	 * 关闭写索引
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		writer.close();
	}

	/**
	 * 索引指定目录的所有文件
	 * 
	 * @param dataDir
	 * @throws Exception
	 */
	public int index(String dataDir) throws Exception {
		Files.list(Paths.get(dataDir)).forEach((path) -> {
			try {
				indexFile(path.toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return writer.numDocs();
	}

	/**
	 * 索引指定文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	private void indexFile(File file) throws IOException {
		System.out.println("索引文件：" + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	/**
	 * 获取文档，文档里再设置每个字段
	 * 
	 * @param file
	 * @throws IOException
	 */
	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		document.add(new TextField("contents", Files.newBufferedReader(file.toPath())));
		document.add(new TextField("fileName", file.getName(), Field.Store.YES));
		document.add(new TextField("fullPath", file.getAbsolutePath(), Store.YES));
		return document;
	}

	public static void main(String[] args) throws Exception {
		String indexDir = Constants.BASE_DIR + "\\lucene1";
		String dataDir = Constants.BASE_DIR + "\\lucene1\\data";

		long start = System.currentTimeMillis();
		Indexer indexer = null;
		int numIndexed = 0;
		try {
			indexer = new Indexer(indexDir);
			numIndexed = indexer.index(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();
		System.out.println("索引：" + numIndexed + " 个文件花费了: " + (end - start));
	}
}
