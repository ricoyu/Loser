package com.loserico.search.lucene.course.lucene6;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.loserico.search.lucene.course.Constants;

public class SmartCNSearcherTest {

	@Test
	public void testSmartSearch() throws IOException, ParseException {
		Directory directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene6"));
		String q = "南京城市";
//		String q = "南京文化";
//		String q = "南京";
		
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new SmartChineseAnalyzer();
		QueryParser queryParser = new QueryParser("desc", analyzer);
		Query query = queryParser.parse(q);

		long start = System.currentTimeMillis();
		TopDocs hits = indexSearcher.search(query, 10);
		long end = System.currentTimeMillis();
		System.out.println("匹配 " + q + ", 总共花费 " + (end - start) + " 毫秒，查询到 " + hits.totalHits + " 个记录");

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("desc"));
		}

		indexReader.close();
	}
	
	@Test
	public void testHighLightSearch() throws IOException, ParseException, InvalidTokenOffsetsException {
		Directory directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, "lucene6"));
//		String q = "南京城市";
		String q = "南京文明";
//		String q = "南京文化";
//		String q = "南京";
		
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new SmartChineseAnalyzer();
		QueryParser queryParser = new QueryParser("desc", analyzer);
		Query query = queryParser.parse(q);
		
		long start = System.currentTimeMillis();
		TopDocs hits = indexSearcher.search(query, 10);
		long end = System.currentTimeMillis();
		System.out.println("匹配 " + q + ", 总共花费 " + (end - start) + " 毫秒，查询到 " + hits.totalHits + " 个记录");
		
		QueryScorer scorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
//		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter();
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("city"));
			String desc = document.get("desc");
			System.out.println(desc);
			if(desc != null) {
				TokenStream tokenStream = analyzer.tokenStream("desc", new StringReader(desc));
				String summary = highlighter.getBestFragment(tokenStream, desc);
				System.out.println(summary);
			}
		}
		
		indexReader.close();
	}
}
