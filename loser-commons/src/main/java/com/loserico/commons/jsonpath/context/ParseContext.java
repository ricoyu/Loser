package com.loserico.commons.jsonpath.context;

import static com.jayway.jsonpath.internal.Utils.notEmpty;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.internal.ParseContextImpl;

public class ParseContext extends ParseContextImpl{
	
	private Configuration configuration = Configuration.defaultConfiguration();

    @Override
    public DocumentContext parse(String json) {
        notEmpty(json, "json string can not be null or empty");
        Object obj = configuration.jsonProvider().parse(json);
        return new JsonContext(obj, configuration);
    }
}
