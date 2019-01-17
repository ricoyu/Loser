package com.loserico.generic;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.loserico.commons.jsonpath.JsonPathUtils;
import com.loserico.commons.utils.ReflectionUtils;
import com.loserico.io.utils.IOUtils;

public class TypeTokenTest {
	static {
		Configuration.setDefaults(new Configuration.Defaults() {

			//需要com.fasterxml.jackson.core:jackson-databind:2.4.5
			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public Set<Option> options() {
				return Sets.newHashSet(Option.SUPPRESS_EXCEPTIONS);
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		// 生成List<T> 中的 List<T>
//		Type listType = new ParameterizedTypeImpl(List.class, new Class[] { Nursery.class });
//		Type type = new ParameterizedTypeImpl(TypeRef.class, new Type[] { listType. });
//		TypeRef<List<?>> typeRef = new TypeRef<List<?>>() {
//		};
//		ReflectionUtils.setField(typeRef, "type", type);
		String json = IOUtils.readClassPathFile("nursery.json");
//		JsonPath.parse(IOUtils.readClassPathFile("nursery.json")).read("$", typeRef);
//		JsonPathUtils.readListNode(json, "$", typeRef);
	}
}
