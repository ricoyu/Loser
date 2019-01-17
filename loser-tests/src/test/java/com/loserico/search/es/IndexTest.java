package com.loserico.search.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;

/**
 * Elasticsearch 2.3.5版本 索引相关 测试
 * <p>
 * Copyright: Copyright (c) 2018-01-11 21:17
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class IndexTest {

	@Test
	public void testCreateGeoIndex() {
		CreateIndexResponse response = ESHelper.client.admin()
				.indices()
				.prepareCreate("geo_statistic")
				.get();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testDeleteGeoIndex() {
		DeleteIndexResponse response = ESHelper.client.admin()
				.indices()
				.prepareDelete("geo_statistic")
				.get();
		System.out.println(response.isAcknowledged());
	}

	@Test
	public void testCreateGeoIndexWithSettings() {
		CreateIndexResponse response = ESHelper.client.admin()
				.indices()
				.prepareCreate("geo_statistic")
				.setSettings(Settings.builder()
						.put("index_number_of_shards", 3)
						.put("index_number_of_replicas", 2))
				.get();
		System.out.println(response.isAcknowledged());
	}
	
	@Test
	public void testCreateGeoIndexWithMapping() {
		CreateIndexResponse response = ESHelper.client.admin()
				.indices()
				.prepareCreate("geo_statistic")
				.setSettings(Settings.builder()
						.put("index_number_of_shards", 3)
						.put("index_number_of_replicas", 2))
				.addMapping("geoType1", IOUtils.readClassPathFile("geo_type1_mapping.json"))
				.get();
		System.out.println(response.isAcknowledged());
	}
}
