package org.loser.search;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.search.option.UpdateOption;
import com.loserico.search.response.BulkResponse;
import com.loserico.search.response.GetResponse;
import com.loserico.search.response.IndexResponse;
import com.loserico.search.response.UpdateResponse;
import com.loserico.search.utils.SearchUtils;

public class SearchUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(SearchUtilsTest.class);
	
	@BeforeClass
	public static void testInit() {
		SearchUtils.warmUp();
	}

	@Test
	public void testIndex() {
		Posts posts = new Posts();
		posts.setId(1L);
		posts.setMessage("trying out Elasticsearch");
		posts.setPostDate(LocalDate.of(2013, 1, 30));
		posts.setUser("三少爷很屌");

		IndexResponse indexResponse = SearchUtils.index(posts);
		String index = indexResponse.getIndex();
		System.out.println("Index: " + index);
		String type = indexResponse.getType();
		System.out.println("Type: " + type);
		String id = indexResponse.getId();
		System.out.println("Id: " + id);
		long version = indexResponse.getVersion();
		System.out.println("Version: " + version);
		if (indexResponse.isCreated()) {
			System.out.println("Created");
		} else if (indexResponse.isUpdated()) {
			System.out.println("Updated");
		}
	}

	@Test
	public void testGetIndex() {
		GetResponse getResponse = SearchUtils.get("posts", "doc", 1);
		if (getResponse.isExists()) {
			System.out.println("成功获取文档");
			System.out.println("Source是否存在: " + getResponse.isExists());
			System.out.println("Index: " + getResponse.getIndex());
			System.out.println("Type: " + getResponse.getType());
			System.out.println("Id: " + getResponse.getId());
			System.out.println("Version: " + getResponse.getVersion());
			System.out.println("Source: " + getResponse.getSource());
			System.out.println("toString: " + getResponse.toString());
			Posts posts = getResponse.toObject(Posts.class);
			System.out.println(toJson(posts));
		} else {
			logger.warn("文档不存在 Index[{}], Type[{}], Id[{}]", "posts", "doc", 1);
		}
	}

	@Test
	public void testUpdate() {
		String json = "{\"user\": \"三少爷很屌\", \"postDate\": \"2013-01-31\", \"message\": \"trying out Elasticsearch\"}";
		UpdateResponse updateResponse = SearchUtils.update("posts", "doc", 11,
				json,
				UpdateOption.fetchSource(true).timeout(2).upsert(false));
		System.out.println(toJson(updateResponse));
	}

	@Test
	public void testExists() {
		boolean exists = SearchUtils.exists("posts", "doc", 1);
		System.out.println(exists);
		System.out.println(SearchUtils.exists("posts", "doc", 11));
	}

	@Test
	public void testDelete() {
		SearchUtils.delete("posts", "doc", 2);
	}
	
	@Test
	public void testBulkIndex() {
		Posts posts = new Posts();
		posts.setId(11L);
		posts.setMessage("trying out Elasticsearch11");
		posts.setPostDate(LocalDate.of(2013, 1, 30));
		posts.setUser("三少爷很屌");
		
		Posts posts2 = new Posts();
		posts2.setId(12L);
		posts2.setMessage("trying out Elasticsearch12");
		posts2.setPostDate(LocalDate.of(2013, 1, 30));
		posts2.setUser("三少爷非常屌");
		BulkResponse bulkResponse = SearchUtils.bulkIndex(asList(posts, posts2));
		System.out.println(toJson(bulkResponse));
	}
	
	@Test
	public void testMultiGet() {
		long begin = System.currentTimeMillis();
		List<Posts> posts = SearchUtils.multiGet(Posts.class, asList(1, 11, 12));
//		for (Posts posts2 : posts) {
//			System.out.println(toJson(posts2));
//		}
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
	}
	
	@Test
	public void testMultiGet2() {
		long begin = System.currentTimeMillis();
		List<Posts> posts = SearchUtils.multiGet(Posts.class, 1, 11, 12, 13);
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
		posts.forEach((post) -> System.out.println(toJson(post)));
	}
	
	@Test
	public void testGetAll() {
		SearchUtils.getAll(Posts.class);
	}

}
