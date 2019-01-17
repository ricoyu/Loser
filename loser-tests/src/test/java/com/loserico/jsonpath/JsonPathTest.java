package com.loserico.jsonpath;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.jsonpath.JsonPathUtils;
import com.loserico.io.utils.IOUtils;

import net.minidev.json.JSONArray;

/**
 * Operator		Description
 * $			The root element to query. This starts all path expressions.
 * 				json串的根元素,不管json是数组还是对象形式
 * @			The current node being processed by a filter predicate.
 * 				代表当前正在处理的item
 * *			Wildcard. Available anywhere a name or numeric are required.
 * ..			Deep scan. Available anywhere a name is required.
 * .<name>		Dot-notated child
 * [start:end]	Array slice operator
 * [?(<expression>)]		Filter expression. Expression must evaluate to a boolean value.
 * 							过滤器很有用
 * ['<name>' (, '<name>')]	Bracket-notated child or children
 * [<number> (, <number>)]	Array index or indexes
 * 
 * 示例：
 * $.store.book[0].title
 * 或者
 * $['store']['book'][0]['title']
 * 
 * @on
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-08-12 15:51
 * @version 1.0
 *
 */
public class JsonPathTest {

	private static String json;

	@BeforeClass
	public static void setup() {
		json = IOUtils.readClassPathFile("jsonpath-example.json");
	}

	/**
	 * Filter Operators
	 * 
	 * @代表当前正在处理的item
	 * 字符串要用单引号或者双引号括起来
	 * 
	 * Filters are logical expressions used to filter arrays. 
	 * A typical filter would be [?(@.age > 18)] where @ represents the current item being processed. 
	 * More complex filters can be created with logical operators && and ||. 
	 * String literals must be enclosed by single or double quotes ([?(@.color == 'blue')] or [?(@.color == "blue")]).
	 * 
	 * Operator	Description
	 * ==	left is equal to right (note that 1 is not equal to '1')
	 * !=	left is not equal to right
	 * <	left is less than right
	 * <=	left is less or equal to right
	 * >	left is greater than right
	 * >=	left is greater than or equal to right
	 * =~	left matches regular expression [?(@.name =~ /foo.*?/i)]
	 * in	left exists in right [?(@.size in ['S', 'M'])]
	 * nin	left does not exists in right
	 * subsetof	left is a subset of right [?(@.sizes subsetof ['S', 'M', 'L'])]
	 * size	size of left (array or string) should match right
	 * empty	left (array or string) should be empty
	 * 
	 * @on
	 */
	@Test
	public void testFilterOperators() {

	}


	/*
	 * The authors of all books
	 */
	@Test
	public void testTheAuthorsOfAllBbooks() {
		//		List<String> authors = JsonPath.read(json, "$.store['book'].*.author");
		//		List<String> authors = JsonPath.read(json, "$.store.book.*.author");
		List<String> authors = JsonPath.read(json, "$.store.book[*].author");
		authors.forEach(System.out::println);
	}

	/*
	 * All authors
	 */
	@Test
	public void testAllAuthors() {
		List<String> authors = JsonPath.read(json, "$..author");
		authors.forEach(System.out::println);
	}

	/*
	 * All things, both books and bicycles
	 */
	@Test
	public void testAllThingsBookAndBycycle() {
		Object result = JsonPath.read(json, "$.store.*");
		System.out.println(result);
	}

	/*
	 * The price of everything
	 */
	@Test
	public void testPriceOfEverything() {
		//		List<Double> prices = JsonPath.read(json, "$.store.*.price"); //错！
		List<Double> prices = JsonPath.read(json, "$.store..price");
		prices.forEach(System.out::println);
	}

	/*
	 * The third book
	 */
	@Test
	public void testThirdBook() {
		Object thirdBook = JsonPath.read(json, "$..book[2]");
		//		Object thirdBook = JsonPath.read(json, "$.store.book[2]");
		System.out.println(thirdBook);
	}

	/*
	 * The second to last book
	 */
	@Test
	public void testSecond2LastBook() {
		Object result = JsonPath.read(json, "$..book[-2]");
		//		Object result = JsonPath.read(json, "$.store.book[-2]");
		System.out.println(result);
	}

	/*
	 * The first two books
	 */
	@Test
	public void testFirstTwoBooks() {
		Object result = JsonPath.read(json, "$..book[0, 1]");
		System.out.println(result);
	}

	/*
	 * All books from index 0 (inclusive) until index 2 (exclusive)
	 */
	@Test
	public void testFromIndex0to2() {
		Object result = JsonPath.read(json, "$..book[:2]");
		System.out.println(result);
	}

	/*
	 * All books from index 1 (inclusive) until index 2 (exclusive)
	 */
	@Test
	public void testFromIndex1To2() {
		Object result = JsonPath.read(json, "$..book[1:2]");
		System.out.println(result);
	}

	/*
	 * Last two books
	 */
	@Test
	public void testLastTwoBooks() {
		Object result = JsonPath.read(json, "$..book[-2:]");
		System.out.println(result);
	}

	/*
	 * Book number two from tail
	 */
	@Test
	public void testBookFromIndex2ToTail() {
		Object result = JsonPath.read(json, "$..book[2:]");
		System.out.println(result);
	}

	/*
	 * All books with an ISBN number
	 */
	@Test
	public void testAllBookWithISBN() {
		JSONArray jsonArray = JsonPath.read(json, "$..book[?(@.isbn)]");
		System.out.println(jsonArray);
		List<Book> books = JacksonUtils.toList(jsonArray.toString(), Book.class);
		books.forEach(b -> System.out.println(toJson(b)));
	}

	/*
	 * All books in store cheaper than 10
	 */
	@Test
	public void testAllBookCheaperThan10() {
		JSONArray jsonArray = JsonPath.read(json, "$..book[?(@.price < 10)]");
		System.out.println(jsonArray);
	}

	/*
	 * All books in store that are not "expensive"
	 */
	@Test
	public void testAllBooksAreNotExpensive() {
		//		JSONArray jsonArray = JsonPath.read(json, "$..book[?(@.price < $.expensive)]");
		JSONArray jsonArray = JsonPath.read(json, "$..book[?(@.price < $['expensive'])]");
		System.out.println(jsonArray);
	}

	/*
	 * All books matching regex (ignore case)
	 */
	@Test
	public void testallBookMatchingRegex() {
		JSONArray jsonArray = JsonPath.read(json, "$..book[?(@.author =~ /.*REES/i)]");
		System.out.println(jsonArray);
	}

	/*
	 * Give me every thing
	 */
	@Test
	public void testGiveMeEverything() {
		JSONArray jsonArray = JsonPath.read(json, "$..*");
		System.out.println(jsonArray);
	}

	/*
	 * The number of books
	 */
	@Test
	public void testtheNumberOfBooks() {
		Object number = JsonPath.read(json, "$..book.length()");
		System.out.println(number);
	}
	
	@Test
	public void testtheBooks() {
		Object number = JsonPath.read(json, "$..book");
		System.out.println(number);
	}

	@Test
	public void testIfNodeExists() {
		String jsonStr = IOUtils.readClassPathFile("error-response.json");
		Object result = JsonPath.read(jsonStr, "$.[?(@.error)]");
		System.out.println(result);
		JSONArray jsonArray = JsonPath.read(json, "$.[?(@.error)]");
		System.out.println(jsonArray.size() == 0);
	}

	/*
	 * If you only want to read once this is OK. In case you need to read an other
	 * path as well this is not the way to go since the document will be parsed every
	 * time you call JsonPath.read(...). To avoid the problem you can parse the json
	 * first.
	 */
	@Test
	public void testParseJsonFirst() {
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
		String author0 = JsonPath.read(document, "$..book[0].author");
		String author1 = JsonPath.read(document, "$..book[1].author");
		System.out.println(author0);
		System.out.println(author1);
	}
	
	/*
	 * 返回由isbn的book的author
	 */
	@Test
	public void testFluentAPI() {
		ReadContext context = JsonPath.parse(json);
		List<String> authorOfBookWithISBN = context.read("$..book[?(@.isbn)].author");
		authorOfBookWithISBN.forEach(System.out::println);

		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
	}
	
	@Test
	public void testConfiguration() {
		Configuration configuration = Configuration.defaultConfiguration();
		configuration.setOptions(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
	}
	
	@Test
	public void testConfigDefaultPathToNull() {
		Configuration conf = Configuration.defaultConfiguration();
		
		json = IOUtils.readClassPathFile("default-path-leaf-to-null.json");
		//OK
		String gender0 = JsonPath.using(conf).parse(json).read("$[0].gender");
		System.out.println(gender0);
		//抛 com.jayway.jsonpath.PathNotFoundException
		//String gender1 = JsonPath.using(conf).parse(json).read("$[1].gender");
		
		Configuration config2 = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
		gender0 = JsonPath.using(config2).parse(json).read("$[0].gender");
		//System.out.println(gender0);
		//这次不会抛异常，拿到值为null
		String gender1 = JsonPath.using(config2).parse(json).read("$[1].gender");
		//System.out.println(gender1);
		
		/*
		 * SUPPRESS_EXCEPTIONS 
		 * This option makes sure no exceptions are propagated from path evaluation. It follows these simple rules:
		 * 
		 * If option ALWAYS_RETURN_LIST is present an empty list will be returned
		 * If option ALWAYS_RETURN_LIST is NOT present null returned
		 * @on
		 */
		Configuration config3 = conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
		gender0 = JsonPath.using(config3).parse(json).read("$[0].gender");
		System.out.println(gender0);
		//这次不会抛异常，拿到值为null
		String gender3 = JsonPath.using(config3).parse(json).read("$[1].gender");
		System.out.println(gender3);
	}
	
	/*
	 * JsonProvider SPI
	 * 
	 * JsonPath is shipped with three different JsonProviders:
	 * 
	 * JsonSmartJsonProvider (default)
	 * JacksonJsonProvider
	 * JacksonJsonNodeJsonProvider
	 * GsonJsonProvider
	 * JsonOrgJsonProvider
	 * 
	 * Changing the configuration defaults as demonstrated should only be done when your application is being initialized. 
	 * Changes during runtime is strongly discouraged, especially in multi threaded applications.
	 * 
	 * @on
	 */
	@Test
	public void testJsonSPIProvider() {
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
		
		String title = JsonPath.read(json, "$.store.book[0].title");
		System.out.println(title);
		
		Object result = JsonPath.read(json, "$.store.book");
		System.out.println(result);
	}
	
	@Test
	public void testReadListOfBooks() {
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
		TypeRef<List<Book>> typeRef = new TypeRef<List<Book>>() {
		};
		List<Book> books =  JsonPath.parse(json).read("$.store.book", typeRef);
		books.forEach(System.out::println);
	}
	
	@Test
	public void testReadListOfBooks2() {
//		List<Book> books = JacksonUtils.toList(json, "$.store.book", new TypeRef<List<Book>>() {
//		});
//		books.forEach(System.out::println);
	}
	
	@Test
	public void testGetBookTitle() {
		String title = JsonPath.read(json, "$.store.book[0].title");
		System.out.println(title);
		
		List<String> titles = JsonPath.read(json, "$.store.*");
		titles.forEach(System.out::println);
	}
	
	@Test
	public void testAliDayuSeccuess() {
//		String json = IOUtils.readClassPathFile("alidayu-fail.json");
		String json = IOUtils.readClassPathFile("alidayu-success.json");
		Object result = JsonPath.read(json, "$..result.success");
		Boolean success = JsonPathUtils.readNode(json, "$..result.success");
		System.out.println(result);
		System.out.println(success);
	}
	
	@Test
	public void testReadBaiduMapResult() {
		String json = IOUtils.readClassPathFile("baidu.json");
		int status = JsonPath.read(json, "$.status");
		
		System.out.println(status);
		
		if(status == 0) {
			String address = JsonPathUtils.readNode(json, "$.result.formatted_address");
			System.out.println(address);
		}
	}
	
	@Test
	public void testReadSource() {
		String json = IOUtils.readClassPathFile("nurseling.json");
		JsonPathUtils.readNode(json, "hits.hits[*]._source");
		List<Nurseling> nurselings = JsonPathUtils.readListNode(json, "hits.hits[*]._source", Nurseling.class);
		System.out.println(nurselings.size());
	}
	
	@Test
	public void testFilterStudentIdNameNotInGivenList() {
		String students = IOUtils.readClassPathFile("withdrawnStudents.json");
//		String students = IOUtils.readClassPathFile("students.json");
		List<Long> studentIds = JsonPathUtils.readListNode(students, "$.data[*].id", Long.class);
		
		String studentIdsStr = join(studentIds.stream().sorted().collect(toList()), ", ");
		System.out.println(studentIds.size());
		System.out.println(studentIdsStr);
	}
}
