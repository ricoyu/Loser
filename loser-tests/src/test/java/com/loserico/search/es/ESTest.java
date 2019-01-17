package com.loserico.search.es;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.BeforeClass;
import org.junit.Test;

import com.loserico.search.es.model.ProductModel;

public class ESTest {

	private static final String INDEX_NAME = "mytest9";
	private static final String DOC_TYPE = "product";

	public static Client client = null;

	@BeforeClass
	public static void setup() {
		Settings settings = Settings.builder()
				/*
				 * 指定集群的名称
				 * 就算是单节点的elasticsearch，启动后也会生成一个集群的名字，默认为elasticsearch
				 * 
				 * 配置项为cluster.name: my-application
				 * @on
				 */
				.put("cluster.name", "sexy-uncle")
//				.put("cluster.name", "architect")
				.put("shield.user", "rico:12345678")
				//探测集群中机器的状态
				.put("client.transport.sniff", true)
				.build();

		try {
			client = TransportClient.builder()
					.settings(settings).build()
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName("192.168.102.103"), 9300));
//			new InetSocketTransportAddress(InetAddress.getByName("192.168.102.109"), 9300));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIndexCreate() {
		IndicesAdminClient indicesAdminClient = client.admin().indices();
		CreateIndexResponse response = indicesAdminClient.prepareCreate(INDEX_NAME)
				.setSettings(Settings.builder()
						.put("index.number_of_shards", 3)
						.put("index.number_of_replicas", 1))
				.execute()
				.actionGet();
		response.isAcknowledged();
	}

	@Test
	public void testCreateData() {
		List<String> cats = asList("3c", "computer");

		ProductModel productModel = new ProductModel("p1", "mac book 笔记本", 12345, cats);
		IndexResponse response = client.prepareIndex(INDEX_NAME, DOC_TYPE)
				.setId(productModel.getUuid())
				.setSource(toJson(productModel))
				.get();
	}

	@Test
	public void testGetData() {
		Map<String, Object> source = client.prepareGet(INDEX_NAME, DOC_TYPE, "p1")
				.get().getSource();
		System.out.println(toJson(source));
	}

	@Test
	public void testSearch() {
		SearchRequestBuilder search = client.prepareSearch(INDEX_NAME)
				.setTypes(DOC_TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.termQuery("name", "笔记本")) // Query
				.setPostFilter(QueryBuilders.rangeQuery("price").from(12343).to(12346)) // Filter
				.setFrom(0).setSize(60);
		//.setExplain(true)
		//		        .get();
		System.out.println("query==" + search);
		SearchResponse response = search.get();
		response.getHits().getTotalHits();
		Arrays.stream(response.getHits().getHits()).forEach((searchHit) -> {
			System.out.println(searchHit.getScore());
			System.out.println("name: "+searchHit.getSource().get("name"));
			System.out.println(searchHit.getSourceAsString());
		});
	}
	
	@Test
	public void testUpdate() throws InterruptedException, ExecutionException {
		List<String> cats = asList("3c", "computer");

		ProductModel productModel = new ProductModel("p1", "mac book 笔记本 update", 54321, cats);
		UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, DOC_TYPE, productModel.getUuid());
		updateRequest.doc(toJson(productModel));
		
		UpdateResponse updateResponse = client.update(updateRequest).get();
	}
	
	@Test
	public void testDelete() {
		DeleteResponse deleteResponse = client.prepareDelete(INDEX_NAME, DOC_TYPE, "p1").get();
		assertTrue(deleteResponse.isFound());
	}
	
	@Test
	public void testDeleteType() throws InterruptedException, ExecutionException {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX_NAME);
		DeleteIndexResponse response = client.admin().indices().delete(deleteIndexRequest).get();
		assertTrue(response.isAcknowledged());
	}

}
