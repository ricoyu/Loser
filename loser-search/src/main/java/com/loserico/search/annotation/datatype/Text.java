package com.loserico.search.annotation.datatype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.loserico.search.enums.Analyzer;

/**
 * A field to index full-text values, such as the body of an email or the description of a product. 
 * These fields are analyzed, that is they are passed through an analyzer to convert the string into 
 * a list of individual terms before being indexed. 
 * 
 * The analysis process allows Elasticsearch to search for individual words within each full text field.
 * Text fields are not used for sorting and seldom used for aggregations
 * (although the significant text aggregation is a notable exception).
 * 
 * If you need to index structured content such as email addresses, hostnames, status codes, or tags, 
 * it is likely that you should rather use a keyword field.
 *
 * https://www.elastic.co/guide/en/elasticsearch/reference/6.3/text.html
 * <p>
 * Copyright: Copyright (c) 2018-09-04 15:22
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Text {

	/**
	 * The analyzer which should be used for analyzed string fields, both at
	 * index-time and at search-time (unless overridden by the search_analyzer). 
	 * Defaults to the ik_smart analyzer.
	 * 
	 * @return
	 * @on
	 */
	Analyzer analyzer() default Analyzer.IK_SMART;

	Analyzer searchAnalyzer();

	/**
	 * The index option controls whether field values are indexed. 
	 * It accepts true or false and defaults to true. 
	 * Fields that are not indexed are not queryable.
	 * @on
	 */
	boolean index() default true;

	/**
	 * Text fields may also index term prefixes to speed up prefix searches.
	 * Either or both of min_chars and max_chars may be excluded. 
	 * Both values are treated as inclusive
	 * 
	 * min_chars must be greater than zero, defaults to 2
	 * max_chars must be greater than or equal to min_chars and less than 20, defaults to 5
	 * 
	 * @return int[]
	 * @on
	 */
	int[] indexPrefixes() default { 2, 5 };

	/**
	 * Norms store various normalization factors that are later used at
	 * query time in order to compute the score of a document relatively to
	 * a query.
	 * 
	 * Although useful for scoring, norms also require quite a lot of disk
	 * (typically in the order of one byte per document per field in your
	 * index, even for documents that don’t have this specific field). As a
	 * consequence, if you don’t need scoring on a specific field, you
	 * should disable norms on that field. In particular, this is the case
	 * for fields that are used solely for filtering or aggregations.
	 * 
	 * @return boolean
	 */
	boolean norms() default true;

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
}
