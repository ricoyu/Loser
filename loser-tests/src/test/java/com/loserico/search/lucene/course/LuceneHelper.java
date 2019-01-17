package com.loserico.search.lucene.course;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LuceneHelper {
	private static final Logger logger = LoggerFactory.getLogger(LuceneHelper.class);

	public static IndexWriter getWriter(Directory directory) {
		Analyzer analyzer = new StandardAnalyzer(); //标准分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try {
			return new IndexWriter(directory, config);
		} catch (IOException e) {
			logger.error("msg", e);
			throw new RuntimeException(e);
		}
	}
	
	public static IndexWriter getSmartCNWriter(Directory directory) {
		Analyzer analyzer = new SmartChineseAnalyzer(); //中科院的smartcn分词器
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try {
			return new IndexWriter(directory, config);
		} catch (IOException e) {
			logger.error("msg", e);
			throw new RuntimeException(e);
		}
	}
}
