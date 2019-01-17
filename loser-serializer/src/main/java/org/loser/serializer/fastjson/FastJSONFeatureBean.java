package org.loser.serializer.fastjson;

import static com.alibaba.fastjson.serializer.SerializerFeature.WRITE_MAP_NULL_FEATURES;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

public class FastJSONFeatureBean {

	public FastJSONFeatureBean() {
		JSON.DEFAULT_PARSER_FEATURE = JSON.DEFAULT_PARSER_FEATURE | 
				Feature.AllowISO8601DateFormat.getMask() | WRITE_MAP_NULL_FEATURES;
	}
	
}
