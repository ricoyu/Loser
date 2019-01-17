package com.loserico.search.lucene.course.lession1;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.loserico.search.lucene.course.Constants;

public class Searcher {

	public static void search(String indexDir, String q) throws IOException, ParseException {
		Directory directory = FSDirectory.open(Paths.get(indexDir));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("contents", analyzer);
		Query query = queryParser.parse(q);

		long start = System.currentTimeMillis();
		TopDocs hits = indexSearcher.search(query, 10);
		long end = System.currentTimeMillis();
		System.out.println("匹配 " + q + ", 总共花费 " + (end - start) + " 毫秒，查询到 " + hits.totalHits + " 个记录");

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("fullPath"));
		}

		indexReader.close();
	}

	public static void main(String[] args) throws IOException, ParseException {
		String indexDir = Constants.BASE_DIR + "\\lucene1";
		String q = "Zygmunt-Saloni";
		search(indexDir, q);
	}
}
