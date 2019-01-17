package org.loser.search;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.search.utils.SearchUtils;

public class MappingTest {

	private static final Logger logger = LoggerFactory.getLogger(MappingTest.class);
	
	@BeforeClass
	public static void testInit() {
		SearchUtils.warmUp();
	}

	@Test
	public void testPutMapping() throws IOException {
		PutMappingRequest request = new PutMappingRequest("twitter");
		request.type("tweet");

		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
			builder.startObject("properties");
			{
				builder.startObject("message");
				{
					builder.field("type", "text");
				}
				builder.endObject();
			}
			builder.endObject();
		}
		builder.endObject();
		request.source(builder);
	}
}
