package com.loserico.search.lucene.course.lesson3;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import com.loserico.search.lucene.course.Constants;
import com.loserico.search.lucene.course.LuceneHelper;

/**
 * 根据Term查询，并演示加权操作
 * <p>
 * Copyright: Copyright (c) 2017-10-09 21:31
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class IndexingScoreSearchTest {
	private String ids[] = { "1", "2", "3", "4" };
	private String authors[] = { "Jack", "Marry", "John", "Json" };
	private String positions[] = { "accounting", "technician", "salesperson", "boss" };
	private String titles[] = { "Java is a good language.", "Java is a cross platform language", "Java powerful",
			"You should learn java" };
	private String contents[] = {
			"If possible, use the same JRE major version at both index and search time.",
			"When upgrading to a different JRE major version, consider re-indexing. ",
			"Different JRE major versions may implement different versions of Unicode,",
			"For example: with Java 1.4, `LetterTokenizer` will split around the character U+02C6,"
	};

	private Directory directory;

	@Before
	public void setUp() throws IOException {
		directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene3"));
	}

	/**
	 * 生成索引
	 * 
	 * @throws IOException
	 */
	@Test
	public void testIndex() throws IOException {
		IndexWriter writer = LuceneHelper.getWriter(directory);
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			//StringField不会进行分词
			doc.add(new StringField("id", ids[i], Store.YES));
			doc.add(new StringField("author", authors[i], Store.YES));
			doc.add(new StringField("position", positions[i], Store.YES));
			//TextField会分词
			doc.add(new TextField("title", titles[i], Store.YES));
			doc.add(new TextField("content", contents[i], Store.NO));

			writer.addDocument(doc);
		}
		writer.close();
	}
	
	@Test
	public void testIndexWithScore() throws IOException {
		IndexWriter writer = LuceneHelper.getWriter(directory);
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			//StringField不会进行分词
			doc.add(new StringField("id", ids[i], Store.YES));
			doc.add(new StringField("author", authors[i], Store.YES));
			doc.add(new StringField("position", positions[i], Store.YES));
			//TextField会分词, field的术语叫“域”
			TextField field = new TextField("title", titles[i], Store.YES);
			if("boss".equals(positions[i])) {
				//field.setBoost(1.5f); //权重，默认1，这里1.5表示加权
			}
			doc.add(field);
			doc.add(new TextField("content", contents[i], Store.NO));
			
			writer.addDocument(doc);
		}
		writer.close();
	}

	@Test
	public void testSearch() throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		String searchField = "title";
		String q = "java";
		Term term = new Term(searchField, q);
		Query query = new TermQuery(term);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(format("匹配'{0}'，总共查询到{1}个文档", q, hits.totalHits));
		
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("author"));
		}
		
		reader.close();
	}
}
