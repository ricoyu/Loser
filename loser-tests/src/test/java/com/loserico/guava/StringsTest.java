package com.loserico.guava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * http://www.baeldung.com/guava-joiner-and-splitter-tutorial
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-06-26 17:49
 * @version 1.0
 *
 */
public class StringsTest {

	/*
	 * @of
	 * 用分隔符把字符串序列连接起来也可能会遇上不必要的麻烦如果字符串序列中含有null，那连接操作会更难Fluent风格的Joiner让连接字符串更简单
	 * 上述代码返回"Harry; Ron; Hermione"
	 * 
	 * 另外，useForNull(String)方法可以给定某个字符串来替换null，而不像skipNulls()方法是直接忽略null
	 * Joiner也可以用来连接对象类型，在这种情况下，它会把对象的toString()值连接起来
	 * 
	 * 警告:joiner实例总是不可变的用来定义joiner目标语义的配置方法总会返回一个新的joiner实例这使得joiner实例都是线程安全的，
	 * 你可以将其定义为static final常量
	 * @on
	 */
	@Test
	public void testJoiner() {
		Joiner joiner = Joiner.on("; ").skipNulls();
		String result = joiner.join("Harry", null, "Ron", "Hermione");
		System.out.println(result);

		result = Joiner.on(",").join(Arrays.asList(1, 5, 7)); // returns "1,5,7"
		System.out.println(result);
	}

	@Test
	public void whenConvertListToString_thenConverted() {
		List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
		String result = Joiner.on(",").join(names);

		assertEquals(result, "John,Jane,Adam,Tom");
	}

	@Test
	public void whenConvertMapToString_thenConverted() {
		Map<String, Integer> salary = Maps.newHashMap();
		salary.put("John", 1000);
		salary.put("Jane", 1500);
		String result = Joiner.on(" , ").withKeyValueSeparator(" = ")
				.join(salary);

		//	    assertThat(result, containsString("John = 1000"));
		//	    assertThat(result, containsString("Jane = 1500"));
	}

	@Test
	public void whenCreateListFromString_thenCreated() {
		String input = "apple - banana - orange";
		List<String> result = Splitter.on("-").trimResults()
				.splitToList(input);

//		assertThat(result, contains("apple", "banana", "orange"));
	}
	
	@Test
	public void whenCreateMapFromString_thenCreated() {
	    String input = "John=first,Adam=second";
	    Map<String, String> result = Splitter.on(",")
	                                         .withKeyValueSeparator("=")
	                                         .split(input);
	 
	    assertEquals("first", result.get("John"));
	    assertEquals("second", result.get("Adam"));
	}
	
	@Test
	public void whenSplitStringOnMultipleSeparator_thenSplit() {
	    String input = "apple.banana,,orange,,.";
	    List<String> result = Splitter.onPattern("[.|,]")
	                                  .omitEmptyStrings()
	                                  .splitToList(input);
	 
//	    assertThat(result, contains("apple", "banana", "orange"));
	}

	/*
	 * @of
	 * 拆分器[Splitter] 
	 * 
	 * JDK内建的字符串拆分工具有一些古怪的特性比如，String.split悄悄丢弃了尾部的分隔符
	 * 问题: ",a,,b,".split(",")返回？
	 * 
	 * 1 "", "a", "", "b", "" 
	 * 2 null, "a", null, "b", null 
	 * 3 "a", null, "b" 
	 * 4 "a", "b" 
	 * 
	 * 以上都不对
	 * 正确答案是
	 * 5 "", "a", "", "b"
	 * 只有尾部的空字符串被忽略了
	 * 
	 * Splitter使用令人放心的、直白的流畅API模式对这些混乱的特性作了完全的掌控
	 * 
	 * 上述代码返回Iterable<String>，其中包含"foo"、"bar"和"qux"Splitter可以被设置为按照任何模式、字符、
	 * 字符串或字符匹配器拆分
	 * 
	 * 拆分器工厂
	 * 
	 * 	方法						描述				范例
	 * 	Splitter.on(char)		按单个字符拆分		Splitter.on(‘;’)
	 * 	Splitter.on(CharMatcher)按字符匹配器拆分	Splitter.on(CharMatcher.BREAKING_WHITESPACE)
	 * 	Splitter.on(String)		按字符串拆分		Splitter.on(“,   “)
	 * 	Splitter.on(Pattern) 	按正则表达式拆分	Splitter.onPattern(“\r?\n”)
	 * 	Splitter.onPattern(String)	
	 * 	Splitter.fixedLength(int)按固定长度拆分	Splitter.fixedLength(3)
	 * 							最后一段可能比给定长度短，但不会为空	
	 * 
	 * 拆分器修饰符
	 * 
	 * 方法						描述
	 * omitEmptyStrings()		从结果中自动忽略空字符串
	 * trimResults()			移除结果字符串的前导空白和尾部空白
	 * trimResults(CharMatcher)	给定匹配器，移除结果字符串的前导匹配字符和尾部匹配字符
	 * limit(int)				限制拆分出的字符串数量
	 * 
	 * 如果你想要拆分器返回List，只要使用Lists.newArrayList(splitter.split(string))或类似方法。
	 * 警告：splitter实例总是不可变的。用来定义splitter目标语义的配置方法总会返回一个新的splitter实例。
	 * 这使得splitter实例都是线程安全的，你可以将其定义为static final常量。
	 * @on
	 */
	@Test
	public void testSpliter() {
		Splitter.on(',')
				.trimResults()
				.omitEmptyStrings()
				.split("foo,bar,,   qux");
	}
}
