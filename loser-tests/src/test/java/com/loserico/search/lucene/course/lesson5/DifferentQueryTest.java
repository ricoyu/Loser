package com.loserico.search.lucene.course.lesson5;

import static org.elasticsearch.common.lucene.BytesRefs.toBytesRef;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.loserico.search.lucene.course.Constants;

public class DifferentQueryTest {

	private Directory directory;
	private IndexReader reader;
	private IndexSearcher searcher;

	@Before
	public void setUp() throws Exception {
		directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene5"));
		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
	}

	@After
	public void tearDown() throws Exception {
		reader.close();
	}

	/**
	 * 指定项范围搜索
	 * 
	 * @throws IOException
	 */
	@Test
	public void testTermRangeQuery() throws IOException {
		TermRangeQuery query = new TermRangeQuery("desc", toBytesRef("a"), toBytesRef("b"), true,
				true);
		TopDocs hits = searcher.search(query, 10);
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}

	/**
	 * 指定数字范围
	 * 
	 * @throws IOException
	 */
	@Test
	public void testNumericRangeQuery() throws IOException {
		/*NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("id", 1, 3, true, true);
		TopDocs hits = searcher.search(query, 10);
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}*/
	}

	/**
	 * 指定字符串开头搜索
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPrefixQuery() throws IOException {
		PrefixQuery query = new PrefixQuery(new Term("city", "sh"));
		TopDocs hits = searcher.search(query, 10);
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}
	}

	/**
	 * 组合/多条件查询
	 * 
	 * @throws IOException
	 */
	@Test
	public void testBooleanQuery() throws IOException {
		/*NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("id", 1, 2, true, true);
		PrefixQuery query2 = new PrefixQuery(new Term("city", "n"));
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(query1, Occur.MUST);
		builder.add(query2, Occur.MUST);

		TopDocs hits = searcher.search(builder.build(), 10);
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("city"));
			System.out.println(doc.get("desc"));
		}*/
	}
}
