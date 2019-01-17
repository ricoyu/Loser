package com.loserico.search.lucene.course.lession2;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import com.loserico.search.lucene.course.Constants;
import com.loserico.search.lucene.course.LuceneHelper;

public class IndexingTest {

	private String ids[] = { "1", "2", "3" };
	private String citys[] = { "qingdao", "nanjing", "shanghai" };
	private String descs[] = {
			"Qingdao is a beautiful city.",
			"Nanjing is a city of culture.",
			"Shanghai is a bustling city."
	};

	private Directory directory;

	@Before
	public void setUp() throws IOException {
		directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene2"));
	}

	/**
	 * 测试写了几个文档
	 * @throws IOException 
	 */
	@Test
	public void testIndexWriter() throws IOException {
		IndexWriter indexWriter = LuceneHelper.getWriter(directory);
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Store.YES));
			doc.add(new StringField("city", citys[i], Store.YES));
			doc.add(new TextField("desc", descs[i], Store.NO));
			indexWriter.addDocument(doc); //添加文档
		}
		System.out.println("写入了" + indexWriter.numDocs() + "个文档");
		indexWriter.close();
	}
	
	/**
	 * 测试读取文档
	 * @throws IOException
	 */
	@Test
	public void testIndexReader() throws IOException {
		IndexReader indexReader = DirectoryReader.open(directory);
		System.out.println("最大文档数：" + indexReader.maxDoc());
		System.out.println("实际文档时：" + indexReader.numDocs());
		indexReader.close();
	}
	
	/**
	 * 测试删除文档,在合并前
	 * @throws IOException
	 */
	@Test
	public void testDeleteBeforeMerge() throws IOException {
		IndexWriter writer = LuceneHelper.getWriter(directory);
		System.out.println("删除前：" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.commit();
		System.out.println("最大文档数：" + writer.maxDoc());
		System.out.println("实际文档时：" + writer.numDocs());
		writer.close();
	}
	
	/**
	 * 测试删除文档,在合并后
	 * @throws IOException
	 */
	@Test
	public void testDeleteAfterMerge() throws IOException {
		IndexWriter writer = LuceneHelper.getWriter(directory);
		System.out.println("删除前：" + writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		writer.commit();
		writer.forceMergeDeletes(); //强制删除
		System.out.println("最大文档数：" + writer.maxDoc());
		System.out.println("实际文档时：" + writer.numDocs());
		writer.close();
	}
	
	/**
	 * 测试更新文档
	 * @throws IOException
	 */
	@Test
	public void testUpdate() throws IOException {
		IndexWriter writer = LuceneHelper.getWriter(directory);
		Document doc = new Document();
		doc.add(new StringField("id", "1", Store.YES));
		doc.add(new StringField("city", "qingdao", Store.YES));
		doc.add(new TextField("desc", "dsss is a city", Store.YES));
		writer.updateDocument(new Term("id", "1"), doc);
		writer.close();
	}
}
