package com.loserico.commons.jsonpath.context;

import java.lang.reflect.Type;

public interface DocumentContext extends com.jayway.jsonpath.DocumentContext {

    /**
     * Reads the given path from this context
     *
     * Sample code to create a TypeRef
     * <code>
     *       TypeRef ref = new TypeRef<List<Integer>>() {};
     * </code>
     *
     * @param path path to apply
     * @param typeRef  expected return type (will try to map)
     * @param <T>
     * @return result
     */
    <T> T read(String path, Type type);

}
