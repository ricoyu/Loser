package com.loserico.search.annotation.datatype;

import static com.loserico.search.enums.DateFormats.DEFAULT;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.loserico.search.enums.DateFormats;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Dates {

	/**
	 * In JSON documents, dates are represented as strings. Elasticsearch
	 * uses a set of preconfigured formats to recognize and parse these
	 * strings into a long value representing milliseconds-since-the-epoch
	 * in UTC.
	 * 
	 * Besides the built-in formats, your own custom formats can be
	 * specified using the familiar yyyy/MM/dd syntax
	 * 
	 * @return
	 */
	DateFormats format() default DEFAULT;

	/**
	 * The index option controls whether field values are indexed. 
	 * It accepts true or false and defaults to true. 
	 * Fields that are not indexed are not queryable.
	 * @on
	 */
	boolean index() default true;

	/**
	 * By default, field values are indexed to make them searchable, but they are not stored. 
	 * This means that the field can be queried, but the original field value cannot be retrieved.
	 * 
	 * Usually this doesn’t matter. 
	 * The field value is already part of the _source field, which is stored by default. 
	 * If you only want to retrieve the value of a single field or of a few fields, 
	 * instead of the whole _source, then this can be achieved with source filtering.
	 * 
	 * In certain situations it can make sense to store a field. 
	 * For instance, if you have a document with a title, a date, and a very large content field, 
	 * you may want to retrieve just the title and the date without having to extract those fields from a large _source field
	 * 
	 * 默认不会单独存储这个field到index里面，但是可以从_source字段中抽取想要的字段
	 * 如果整个文档内容很多很大，但是你只是想获取某个字段，那么久可以将stored设为true，这样可以直接查询获取这个字段的值，而不是获取很大的_source
	 * 然后从中抽取想要的字段
	 * 
	 * @return boolean
	 * @on
	 */
	boolean store() default false;

	/**
	 * Should the field be stored on disk in a column-stride fashion, so
	 * that it can later be used for sorting, aggregations, or scripting?
	 * Accepts true (default) or false.
	 * 
	 * @return
	 */
	boolean docValues() default true;

}
