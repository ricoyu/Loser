package com.loserico.search.lucene.course.lesson4;

import static java.text.MessageFormat.format;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.loserico.search.lucene.course.Constants;

public class SearchTest {

	private Directory directory;
	private IndexReader reader;
	private IndexSearcher searcher;

	@Before
	public void setUp() throws Exception {
		directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene4"));
		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
	}

	/**
	 * 对特定项搜索
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTermQuery() throws IOException {
		String searchField = "contents";
		String q = "particular";
		//		String q = "java";
		Term term = new Term(searchField, q);
		Query query = new TermQuery(term);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(format("匹配 {0}，总共查询到{1}个文档", q, hits.totalHits));

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("fullPath"));
		}
	}

	/**
	 * 解析查询表达式
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void testQueryParsers() throws ParseException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		String searchField = "contents";
//		String q = "particular Unicode"; //或
//		String q = "particular OR Unicode"; //或
//		String q = "particular";
		String q = "particula~"; //查近似（相近）的词。particula没有匹配，但是与particula相近的可以匹配到四个文档
//		String q = "particular AND CustomSeparatorBreakIterator"; //AND要大写才有效
		QueryParser queryParser = new QueryParser(searchField, analyzer);
		Query query = queryParser.parse(q);
		TopDocs hits = searcher.search(query, 10);
		System.out.println(format("匹配 {0}，查询到 {1}条记录", q, hits.totalHits));

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document document = searcher.doc(scoreDoc.doc);
			System.out.println(document.get("fullPath"));
		}
	}

	@After
	public void tearDown() throws Exception {
		reader.close();
	}

}
