package com.loserico.search.lucene.course.lesson5;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.loserico.search.lucene.course.Constants;
import com.loserico.search.lucene.course.LuceneHelper;

public class Indexer {

	private Integer ids[] = { 1, 2, 3 };
	private String citys[] = { "qingdao", "nanjing", "shanghai" };
	private String descs[] = {
			"Qingdao is a beautiful city.",
			"Nanjing is b city of culture.",
			"Shanghai is c bustling city."
	};

	private Directory directory;
	
	private void index(String indexDir) throws IOException {
		directory = FSDirectory.open(Paths.get(Constants.BASE_DIR, indexDir));
		IndexWriter writer = LuceneHelper.getWriter(directory);
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
//			doc.add(new IntField("id", ids[i], Store.YES));
			doc.add(new StringField("city", citys[i], Store.YES));
			doc.add(new TextField("desc", descs[i], Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		new Indexer().index("lucene5");
	}
}
