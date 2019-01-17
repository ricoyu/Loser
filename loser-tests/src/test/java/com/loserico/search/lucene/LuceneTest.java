package com.loserico.search.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.Test;

public class LuceneTest {

	/**
	 * 建立索引
	 * 为了简单起见，我们下面为一些字符串创建内存索引
	 * @throws IOException 
	 * @on
	 */
	@Test
	public void testIndex() throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new NIOFSDirectory(Paths.get("D:\\opt\\Lucene"));
//		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		try {
			IndexWriter indexWriter = new IndexWriter(index, config);
			addDoc(indexWriter, "Lucene in Action", "193398817");
			addDoc(indexWriter, "Lucene for Dummies", "55320055Z");
			addDoc(indexWriter, "Managing Gigabytes", "55063554A");
			addDoc(indexWriter, "The Art of Computer Science", "9900333X");
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addDoc(IndexWriter writer, String title, String isbn) throws IOException {
		Document doc = new Document();
		/*
		 * 注意，对于需要分词的内容我们使用TextField，对于像id这样不需要分词的内容我们使用StringField。
		 */
		doc.add(new TextField("title", title, Store.YES));
		doc.add(new StringField("isbn", isbn, Store.YES));
		writer.addDocument(doc);
	}
}
