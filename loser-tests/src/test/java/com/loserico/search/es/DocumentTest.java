package com.loserico.search.es;

import org.elasticsearch.action.index.IndexResponse;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;

public class DocumentTest {

	@Test
	public void testCreateDocument() {
		IndexResponse response = ESHelper.client.prepareIndex("geo_statistic", "geoType1")
//				.setSource(IOUtils.readClassPathFile("geo_document_zs.json"))
//				.setSource(IOUtils.readClassPathFile("geo_document_zx.json"))
//				.setSource(IOUtils.readClassPathFile("geo_document_ys.json"))
//				.setSource(IOUtils.readClassPathFile("geo_document_yx.json"))
				.setSource(IOUtils.readClassPathFile("geo_document_zhongxin.json"))
				.get();

		String _index = response.getIndex();
		String _type = response.getType();
		String _id = response.getId();
		long _version = response.getVersion();
		boolean created = response.isCreated();
	}
}
