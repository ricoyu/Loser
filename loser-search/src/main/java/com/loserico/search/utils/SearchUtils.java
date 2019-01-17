package com.loserico.search.utils;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static com.loserico.commons.jackson.JacksonUtils.toObject;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.get.MultiGetResponse.Failure;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.resource.PropertyReader;
import com.loserico.search.annotation.parser.IndexedParser;
import com.loserico.search.exeption.ExistsRequestException;
import com.loserico.search.exeption.GetRequestException;
import com.loserico.search.exeption.MultiGetRequestException;
import com.loserico.search.exeption.RequestIOException;
import com.loserico.search.option.UpdateOption;
import com.loserico.search.response.BulkResponse;
import com.loserico.search.response.DeleteResponse;
import com.loserico.search.response.GetResponse;
import com.loserico.search.response.IndexResponse;
import com.loserico.search.response.UpdateResponse;

/**
 * Elasticsearch 客户端封装
 * <p>
 * Copyright: Copyright (c) 2018-08-17 17:30
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class SearchUtils {

	private static final Logger logger = LoggerFactory.getLogger(SearchUtils.class);

	private static final PropertyReader propertyReader = new PropertyReader("es");

	/**
	 * 缓存已经解析的需要被索引的POJO
	 */
	private static final ConcurrentHashMap<String, IndexedParser> indexParsers = new ConcurrentHashMap<>();

	public static RestHighLevelClient client = null;

	static {
		String schema = propertyReader.getString("es.schema", "http");
		logger.info("ES Schema: {}", schema);

		String hosts = propertyReader.getString("es.host"); //192.168.102.106:9200, 192.168.102.109:9200 这种形式
		if (isBlank(hosts)) {
			throw new RuntimeException("请提供Elasticsearch Host 配置 es.host=ip:port, ip:port");
		}
		logger.info("ES hosts: {}", hosts);

		String[] hostArray = hosts.split(",");
		HttpHost[] httpHosts = Arrays.stream(hostArray)
				.map((hostPort) -> {
					String[] hostAndPort = hostPort.split(":");
					String host = hostAndPort[0].trim();
					int port = Integer.parseInt(hostAndPort[1].trim());
					return new HttpHost(host, port, schema);
				}).toArray(HttpHost[]::new);

		client = new RestHighLevelClient(RestClient.builder(httpHosts));

	}

	@SuppressWarnings("unused")
	public static void warmUp() {
		Class<SearchUtils> clazz = SearchUtils.class;
	}

	public static void close() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

	/**
	 * 将POJO加入索引
	 * 
	 * @param source
	 * @return IndexResponse
	 */
	public static IndexResponse index(Object source) {
		if (source == null) {
			return null;
		}
		Class<? extends Object> clazz = source.getClass();
		IndexedParser indexedParser = indexedParser(clazz);
		IndexRequest request = new IndexRequest(
				indexedParser.getIndex(),
				indexedParser.getType(),
				indexedParser.getId(source));

		String json = JacksonUtils.toJson(source);
		request.source(json, XContentType.JSON);
		try {
			return new IndexResponse(client.index(request, RequestOptions.DEFAULT));
		} catch (IOException e) {
			logger.error("索引操作失败, json:\n " + json, e);
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT) {
				logger.error(e.status().toString(), e);
				return new IndexResponse(true);
			}
		}

		return new IndexResponse();
	}

	/**
	 * 批处理
	 * Action可以是 index, create, delete and update
	 * 原生API的source(Object... source)方法是这样的：
	 * source都需要成对出现，即数量是偶数
	 * 对应了实际存储的文档的某对 field-value
	 * 所以这里的source不是指整个文档，而是指文档的某个字段/值 pair
	 * @return
	 * @on
	 */
	public static BulkResponse bulkIndex(List<?> sources) {
		BulkRequest request = new BulkRequest();
		for (Object source : sources) {
			IndexedParser indexedParser = indexedParser(source.getClass());
			IndexRequest indexRequest = new IndexRequest(indexedParser.getIndex(),
					indexedParser.getType(),
					indexedParser.getId(source));
			indexRequest.source(toJson(source), XContentType.JSON);
			request.add(indexRequest);
		}
		org.elasticsearch.action.bulk.BulkResponse response;
		try {
			response = client.bulk(request);
			BulkResponse bulkResponse = new BulkResponse();

			for (BulkItemResponse bulkItemResponse : response) {
				DocWriteResponse itemResponse = bulkItemResponse.getResponse();

				if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
						|| bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
					if (bulkItemResponse.getResponse().getResult() == Result.CREATED) {
						bulkResponse.createdPlusPlus();
						IndexResponse indexResponse = new IndexResponse((org.elasticsearch.action.index.IndexResponse) itemResponse);
						bulkResponse.add(indexResponse);
					} else if (bulkItemResponse.getResponse().getResult() == Result.UPDATED) {
						bulkResponse.updatedPlusPlus();
						UpdateResponse updateResponse = new UpdateResponse(
								(org.elasticsearch.action.update.UpdateResponse) itemResponse);
						bulkResponse.add(updateResponse);
					}
				} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
					bulkResponse.updatedPlusPlus();
					UpdateResponse updateResponse = new UpdateResponse((org.elasticsearch.action.update.UpdateResponse) itemResponse);
					bulkResponse.add(updateResponse);

				}
			}

			return bulkResponse;
		} catch (IOException e) {
			logger.error("bulk index failed!", e);
			throw new RequestIOException(e);
		}
	}

	/**
	 * 批量更新
	 * 
	 * @param sources
	 * @param updateOption
	 * @return BulkResponse
	 */
	public static BulkResponse bulkUpdate(List<?> sources, UpdateOption updateOption) {
		BulkRequest bulkRequest = new BulkRequest();
		for (Object source : sources) {
			IndexedParser indexedParser = indexedParser(source.getClass());
			UpdateRequest updateRequest = new UpdateRequest(indexedParser.getIndex(),
					indexedParser.getType(),
					indexedParser.getId(source));
			updateRequest.doc(toJson(source), XContentType.JSON);
			updateOption.apply(updateRequest);
			bulkRequest.add(updateRequest);
		}

		org.elasticsearch.action.bulk.BulkResponse response;
		try {
			response = client.bulk(bulkRequest);
			BulkResponse bulkResponse = new BulkResponse();

			for (BulkItemResponse bulkItemResponse : response) {
				DocWriteResponse itemResponse = bulkItemResponse.getResponse();

				if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
						|| bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
					if (bulkItemResponse.getResponse().getResult() == Result.CREATED) {
						bulkResponse.createdPlusPlus();
						IndexResponse indexResponse = new IndexResponse((org.elasticsearch.action.index.IndexResponse) itemResponse);
						bulkResponse.add(indexResponse);
					} else if (bulkItemResponse.getResponse().getResult() == Result.UPDATED) {
						bulkResponse.updatedPlusPlus();
						UpdateResponse updateResponse = new UpdateResponse(
								(org.elasticsearch.action.update.UpdateResponse) itemResponse);
						bulkResponse.add(updateResponse);
					}
				} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
					bulkResponse.updatedPlusPlus();
					UpdateResponse updateResponse = new UpdateResponse((org.elasticsearch.action.update.UpdateResponse) itemResponse);
					bulkResponse.add(updateResponse);

				}
			}

			return bulkResponse;
		} catch (IOException e) {
			logger.error("bulk index failed!", e);
			throw new RequestIOException(e);
		}
	}

	/**
	 * 根据ID获取文档
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return GetResponse
	 */
	public static GetResponse get(String index, String type, Object id) {
		requireNonNull(index, type, id);
		GetRequest request = new GetRequest(index, type, id.toString());
		GetResponse getResponse = new GetResponse();

		try {
			getResponse = new GetResponse(client.get(request));
		} catch (IOException e) {
			logger.error("获取文档 Index[{}], Type[{}], Id[{}] 失败", index, type, id);
			throw new GetRequestException(e);
		}

		return getResponse;
	}

	/**
	 * 获取文档, clazz类必须标注Indexed注解
	 * 
	 * @param clazz
	 * @param id
	 * @return T
	 */
	public static <T> T get(Class<T> clazz, Object id) {
		IndexedParser indexedParser = indexedParser(clazz);
		GetRequest request = new GetRequest(indexedParser.getIndex(), indexedParser.getType(), id.toString());

		try {
			String json = client.get(request).getSourceAsString();
			return JacksonUtils.toObject(json, clazz);
		} catch (IOException e) {
			logger.error("获取文档 Index[{}], Type[{}], Id[{}] 失败", indexedParser.getIndex(), indexedParser.getType(), id);
			throw new GetRequestException(e);
		}
	}

	/**
	 * 批量获取文档
	 * 
	 * @param clazz
	 * @param ids
	 * @return
	 */
	public static <T> List<T> multiGet(Class<T> clazz, List<?> ids) {
		MultiGetRequest request = new MultiGetRequest();
		IndexedParser indexedParser = indexedParser(clazz);
		String index = indexedParser.getIndex();
		String type = indexedParser.getType();
		setMultiGetRequestIds(ids, request, index, type);

		return doMultiGetQuery(clazz, request);
	}

	/**
	 * 批量获取文档
	 * 
	 * @param clazz
	 * @param ids
	 * @return
	 */
	public static <T> List<T> multiGet(Class<T> clazz, Object... ids) {
		MultiGetRequest request = new MultiGetRequest();
		IndexedParser indexedParser = indexedParser(clazz);
		String index = indexedParser.getIndex();
		String type = indexedParser.getType();

		setMultiGetRequestIds(ids, request, index, type);

		return doMultiGetQuery(clazz, request);
	}

	/**
	 * 指定文档是否存在
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return boolean
	 */
	public static boolean exists(String index, String type, Object id) {
		requireNonNull(index, type, id);
		GetRequest getRequest = new GetRequest(index, type, id.toString());
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		try {
			return client.exists(getRequest);
		} catch (IOException e) {
			logger.error("获取文档 Index[{}], Type[{}], Id[{}] 失败", index, type, id);
			throw new ExistsRequestException(e);
		}
	}

	/**
	 * 指定文档是否存在, clazz类必须标注Indexed注解
	 * 
	 * @param clazz
	 * @param id
	 * @return boolean
	 */
	public static boolean exists(Class<?> clazz, Object id) {
		IndexedParser indexedParser = indexedParser(clazz);
		GetRequest getRequest = new GetRequest(indexedParser.getIndex(), indexedParser.getType(), id.toString());
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		try {
			return client.exists(getRequest);
		} catch (IOException e) {
			logger.error("获取文档 Index[{}], Type[{}], Id[{}] 失败", indexedParser.getIndex(), indexedParser.getType(), id);
			throw new ExistsRequestException(e);
		}
	}

	/**
	 * 删除指定文档
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return DeleteResponse
	 */
	public static DeleteResponse delete(String index, String type, Object id) {
		requireNonNull(index, type, id);
		DeleteRequest request = new DeleteRequest(index, type, id.toString());
		try {
			org.elasticsearch.action.delete.DeleteResponse deleteResponse = client.delete(request);
			return new DeleteResponse(deleteResponse);
		} catch (IOException e) {
			logger.error("删除文档 Index[{}], Type[{}], Id[{}] 失败", index, type, id);
			throw new RequestIOException(e);
		}
	}

	/**
	 * 删除指定文档, clazz类必须标注Indexed注解
	 * 
	 * @param clazz
	 * @param id
	 * @return DeleteResponse
	 */
	public static DeleteResponse delete(Class<?> clazz, Object id) {
		IndexedParser indexedParser = indexedParser(clazz);
		DeleteRequest request = new DeleteRequest(indexedParser.getIndex(), indexedParser.getType(), id.toString());
		try {
			org.elasticsearch.action.delete.DeleteResponse deleteResponse = client.delete(request);
			return new DeleteResponse(deleteResponse);
		} catch (IOException e) {
			logger.error("删除文档 Index[{}], Type[{}], Id[{}] 失败", indexedParser.getIndex(), indexedParser.getType(), id);
			throw new RequestIOException(e);
		}
	}

	/**
	 * 用JSON串更新文档
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonDoc
	 * @param updateOption
	 * @return UpdateResponse
	 */
	public static UpdateResponse update(String index, String type, Object id,
			String jsonDoc,
			UpdateOption updateOption) {
		requireNonNull(index, type, id);
		UpdateRequest request = new UpdateRequest(index, type, id.toString());
		request.doc(jsonDoc, XContentType.JSON);

		UpdateResponse updateResponse = new UpdateResponse();
		try {
			if (updateOption.isUpsert()) {
				request.upsert(jsonDoc, XContentType.JSON);
			}
			updateOption.apply(request);

			return new UpdateResponse(client.update(request));
		} catch (IOException e) {
			logger.error("", e);
			throw new RequestIOException(e);
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.NOT_FOUND) {
				updateResponse.setNotFound(true);
			} else if (e.status() == RestStatus.CONFLICT) {
				updateResponse.setConflicted(true);
			}
		}
		return updateResponse;
	}

	public static <T> List<T> getAll(Class<T> clazz) {
		IndexedParser indexedParser = IndexedParser.parse(clazz);
		SearchRequest searchRequest = new SearchRequest(indexedParser.getIndex());
		searchRequest.types(indexedParser.getType());
		//searchRequest.routing("routing"); //Set a routing parameter
		//Use the preference parameter e.g. to execute the search to prefer local shards. The default is to randomize across shards.
		searchRequest.preference("_local");

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
		sourceBuilder.from(0);
		sourceBuilder.size(10);
		sourceBuilder.timeout(new TimeValue(6, TimeUnit.SECONDS));

		searchRequest.source(sourceBuilder);

		//Create a full text Match Query that matches the text "kimchy" over the field "user"
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("user", "kimchy");

		//Enable fuzzy matching on the match query
		matchQueryBuilder.fuzziness(Fuzziness.AUTO);
		//Set the prefix length option on the match query
		matchQueryBuilder.prefixLength(3);
		//Set the max expansion options to control the fuzzy process of the query
		matchQueryBuilder.maxExpansions(10);

		sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); //Sort descending by _score (the default)
		sourceBuilder.sort(new FieldSortBuilder("_uid").order(SortOrder.ASC)); //Also sort ascending by _id field

		sourceBuilder.fetchSource(false);

		/*
		 * The method also accepts an array of one or more wildcard patterns
		 * to control which fields get included or excluded in a more fine
		 * grained way:
		 */
		String[] includeFields = new String[] { "title", "user", "innerObject.*" };
		String[] excludeFields = new String[] { "_type" };
		sourceBuilder.fetchSource(includeFields, excludeFields);

		try {
			SearchResponse searchResponse = client.search(searchRequest);
			System.out.println(toJson(searchResponse));
			return null;
		} catch (IOException e) {
			logger.error("", e);
			throw new RequestIOException(e);
		}
	}

	private static void requireNonNull(Object... args) {
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			Objects.requireNonNull(arg);
		}
	}

	/**
	 * POJO的文档解析器
	 * 
	 * @param clazz
	 * @return
	 */
	private static IndexedParser indexedParser(Class<? extends Object> clazz) {
		IndexedParser indexedParser = indexParsers.computeIfAbsent(clazz.getName(), x -> IndexedParser.parse(clazz).requireLegal());
		return indexedParser;
	}

	private static <T> List<T> doMultiGetQuery(Class<T> clazz, MultiGetRequest request) {
		List<T> results = new ArrayList<>();
		try {
			MultiGetResponse response = client.multiGet(request);
			MultiGetItemResponse[] itemResponses = response.getResponses();

			for (int i = 0; i < itemResponses.length; i++) {
				MultiGetItemResponse multiGetItemResponse = itemResponses[i];

				Failure failure = multiGetItemResponse.getFailure();
				if (failure != null) {
					throw new MultiGetRequestException(failure.getMessage(), failure.getFailure());
				}

				org.elasticsearch.action.get.GetResponse getResponse = multiGetItemResponse.getResponse();
				if (getResponse.isExists()) {
					String sourceAsString = getResponse.getSourceAsString();
					results.add(toObject(sourceAsString, clazz));
				} else {
					logger.warn(getResponse.toString());
				}
			}

			return results;
		} catch (IOException e) {
			logger.error("", e);
			throw new RequestIOException(e);
		}
	}

	private static void setMultiGetRequestIds(List<?> ids, MultiGetRequest request, String index, String type) {
		for (Object id : ids) {
			request.add(new MultiGetRequest.Item(index, type, id.toString()));
		}
	}

	private static void setMultiGetRequestIds(Object[] ids, MultiGetRequest request, String index, String type) {
		for (Object id : ids) {
			request.add(new MultiGetRequest.Item(index, type, id.toString()));
		}
	}

}