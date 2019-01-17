package com.loserico.commons.jsonpath.mapper;
import java.lang.reflect.Type;

import com.jayway.jsonpath.Configuration;

/**
 * Maps object between different Types
 */
public interface MappingProvider extends com.jayway.jsonpath.spi.mapper.MappingProvider{


    /**
     *
     * @param source object to map
     * @param targetType the type the source object should be mapped to
     * @param configuration current configuration
     * @param <T> the mapped result type
     * @return return the mapped object
     */
    <T> T map(Object source, Class<T> targetType, Configuration configuration);

    /**
     *
     * @param source object to map
     * @param targetType the type the source object should be mapped to
     * @param configuration current configuration
     * @param <T> the mapped result type
     * @return return the mapped object
     */
    <T> T map(Object source, Type targetType, Configuration configuration);
}