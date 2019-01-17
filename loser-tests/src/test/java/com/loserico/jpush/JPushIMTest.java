package com.loserico.jpush;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static com.loserico.commons.utils.StringUtils.concat;
import static com.loserico.commons.utils.StringUtils.concatWith;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.BaseEncoding;
import com.loserico.commons.jsonpath.JsonPathUtils;
import com.loserico.io.utils.IOUtils;

public class JPushIMTest {

	private static final Logger logger = LoggerFactory.getLogger(JPushIMTest.class);

	private static String auth;
	private static String baseUrl = "https://api.im.jpush.cn";

	@BeforeClass
	public static void generateAuthStr() {
//		auth = BaseEncoding.base64().encode("bd803432aaade235c271563b:5fb661ec80f9cc4a630e3291".getBytes(UTF_8));
				auth = BaseEncoding.base64().encode("a1d81c17fb2bb650a0c8c1fd:4947cbe14469d4536a7a41dc".getBytes(UTF_8));
	}

	@Test
	public void testCreateUser() {
		try {
			//			HttpResponse response = 
			CloseableHttpResponse response = (CloseableHttpResponse) Request.Post(concat(baseUrl, "/v1/users/"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.bodyString(IOUtils.readClassPathFile("register-users.json"), ContentType.APPLICATION_JSON)
					.useExpectContinue()
					.execute()
					.returnResponse();
			String responseStr = IOUtils.readAsString(response.getEntity().getContent());
			System.out.println(responseStr);
			//			response.getEntity().getContent()
			//					.returnContent()
			//					.toString();
			//			String content = ((CloseableHttpResponse) response).getEntity().getContent();
			//			IOUtils.readLines(((CloseableHttpResponse) response).getEntity().getContent())
			//			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParseResult() {
		String result = "[{\"username\": \"hawk\", \"error\": {\"code\": 899001, \"message\": \"user exist\"} }, {\"username\": \"ricoyucsd\", \"error\": {\"code\": 899001, \"message\": \"user exist\"} }, {\"username\": \"ricoyussss\", \"nickname\": \"三少爷\", \"birthday\": \"1982-11-09\", \"gender\": 1, \"avatar\": \"qiniu/image/j/8D57A18AD6926D6D879DFA36B4ED9CC4.jpg\"} ]";
		JsonPathUtils.readListNode(result, "[?(@.error)].error.message", String.class);
		JsonPathUtils.readListNode(result, "[?(@.error)].['error'].message", String.class);
		List<String> failedUsers = JsonPathUtils.readListNode(result, "[?(@.error)].username", String.class);
		List<String> createdUsers = JsonPathUtils.readListNode(result, "[?(!@.error)].username", String.class);
		failedUsers.forEach(System.out::println);
		System.out.println("==================");
		createdUsers.forEach(System.out::println);
	}

	@Test
	public void testGetUser() {
		/*
		 * RestTemplate restTemplate = new RestTemplate(); HttpHeaders httpHeaders =
		 * new HttpHeaders() {{ String authHeader = "Basic " + auth; set(
		 * "Authorization", authHeader ); }}; ResponseEntity<String> result =
		 * restTemplate.exchange(concat(baseUrl, "/v1/users/ricoyu"), HttpMethod.GET,
		 * new HttpEntity(httpHeaders), String.class); String content =
		 * result.getBody();
		 */
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request.Get(concat(baseUrl, "/v1/users/18862304213jaqndf"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.execute()
					.returnResponse();
			String content = IOUtils.readAsString(response.getEntity().getContent());
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * try { String response = Request.Get(concat(baseUrl, "/v1/users/ricoyu"))
		 * .addHeader("Authorization", concatWith(" ", "Basic", auth)) .execute()
		 * .returnContent() .asString(); System.out.println(response); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */ }

	@Test
	public void testDeleteUser() {
		try {
			Content response = Request.Delete(concat(baseUrl, "/v1/users/ricoyu"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.addHeader("Content-Type", "application/json")
					.execute()
					.returnContent();
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUploadImage() {
		try {
			String content = Request.Post(concat(baseUrl, "/v1/resource?type=image"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.body(MultipartEntityBuilder.create()
							.addBinaryBody("filename", Paths.get("D:\\Dropbox\\图片\\images.jpg").toFile(),
									ContentType.APPLICATION_OCTET_STREAM, "images.jpg")
							.addTextBody("bd803432aaade235c271563b", "5fb661ec80f9cc4a630e3291")
							.build())
					.execute()
					.returnContent()
					.asString();
			logger.info(content);//{"media_id":"qiniu/image/j/8D57A18AD6926D6D879DFA36B4ED9CC4.jpg","media_crc32":2431581959,"width":231,"height":218,"format":"jpg","fsize":6123,"hash":"Fp82w3fhN1znNlbL4lS5v6UyFu4W"}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDownloadImage() {
		try {
			String content = Request
					.Get(concat(baseUrl, "/v1/resource?mediaId=qiniu/image/j/8D57A18AD6926D6D879DFA36B4ED9CC4.jpg"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.execute()
					.returnContent()
					.asString();
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTrim() {
		System.out.println("9365 0245zyydmj".trim().replaceAll(" ", ""));
	}

	@Test
	public void createGroup() {
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request
					.Post(concat(baseUrl, "/v1/groups/"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.bodyString(IOUtils.readClassPathFile("createGroup.json"), ContentType.APPLICATION_JSON)
					.execute()
					.returnResponse();
			String content = IOUtils.readAsString(response.getEntity().getContent());
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetGroup() {
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request
					.Get(concat(baseUrl, "/v1/groups/25688109"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.execute()
					.returnResponse();
			String content = IOUtils.readAsString(response.getEntity().getContent());
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateGroup() {
		try {
			IMGroupVO imGroupVO = new IMGroupVO();
			imGroupVO.setAvatar("/asd/aa.png");
			imGroupVO.setFlag(1);
			CloseableHttpResponse response = (CloseableHttpResponse) Request
					.Put(concat(baseUrl, "/v1/groups/25680041"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.bodyString(toJson(imGroupVO), ContentType.APPLICATION_JSON)
//					.bodyString("{\"avatar\": \"/as/as.png\"}", ContentType.APPLICATION_JSON)
					.execute()
					.returnResponse();
			System.out.println(response.getEntity() == null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void addGroupMembers() {
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request.Post(concat(baseUrl, "/v1/groups/10513458/members"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.bodyString(IOUtils.readClassPathFile("addGroupMembers.json"), ContentType.APPLICATION_JSON)
					.execute()
					.returnResponse();
			System.out.println(response.getEntity() == null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testGetAllUsers() {
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request
					.Get(concat(baseUrl, "/v1/users/?start=0&count=5004"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.execute()
					.returnResponse();
			String content = IOUtils.readAsString(response.getEntity().getContent());
			System.out.println(content);//{"media_id":"qiniu/image/j/8D57A18AD6926D6D879DFA36B4ED9CC4.jpg","media_crc32":2431581959,"width":231,"height":218,"format":"jpg","fsize":6123,"hash":"Fp82w3fhN1znNlbL4lS5v6UyFu4W"}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBatchDeleteUsers() {
		String responseUsers = "{\"count\": 500, \"total\": 714, \"start\": 0, \"users\": [{\"mtime\": \"2018-03-16 18:12:41\", \"gender\": 0, \"username\": \"96289794xcuqzz\", \"ctime\": \"2018-03-16 18:12:41\"}, {\"mtime\": \"2018-03-16 18:12:41\", \"gender\": 0, \"username\": \"96269528oydxvk\", \"ctime\": \"2018-03-16 18:12:41\"} ] }";
		String errorResponse = "{\"error\":{\"code\":899003,\"message\":\"parameter invalid!\"}}";
		if (!JsonPathUtils.ifExists(errorResponse, "$.error")) {
			int count = JsonPathUtils.readNode(responseUsers, "$.count");
			if (count > 0) {
				List<String> usernames = JsonPathUtils.readListNode(responseUsers, "$.users[*].username", String.class);
				String joinedUsernames = join(usernames, ",");
				System.out.println(joinedUsernames);
			}
		}
	}

	@Test
	public void testBatchDeleteAllUsers() {
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) Request
					.Get(concat(baseUrl, "/v1/users/?start=0&count=500"))
					.addHeader("Authorization", concatWith(" ", "Basic", auth))
					.execute()
					.returnResponse();
			String content = IOUtils.readAsString(response.getEntity().getContent());

			if (!JsonPathUtils.ifExists(content, "$.error")) {
				int count = JsonPathUtils.readNode(content, "$.count");
				if (count > 0) {
					List<String> usernames = JsonPathUtils.readListNode(content, "$.users[*].username", String.class);
					int index = 0;
					while (index < usernames.size()) {
						int end = index + 100;
						if (end > usernames.size()) {
							end = usernames.size();
						}
						String joinedUsernames = join(usernames.subList(index, end), ",");
						System.out.println(joinedUsernames);

						CloseableHttpClient httpclient = HttpClients.createDefault();
						HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(concat(baseUrl, "/v1/users"));
						httpDelete.setEntity(
								new StringEntity(join("[", joinedUsernames, "]"), ContentType.APPLICATION_JSON));
						httpDelete.addHeader("Authorization", concatWith(" ", "Basic", auth));
						CloseableHttpResponse deleteResponse = httpclient.execute(httpDelete);
						/*
						 * CloseableHttpResponse deleteResponse =
						 * (CloseableHttpResponse) Request .Delete(concat(baseUrl,
						 * "/v1/users")) .addHeader("Authorization", concatWith(" ",
						 * "Basic", auth)) .body(new StringEntity(join("[",
						 * joinedUsernames, "]"), ContentType.APPLICATION_JSON)) //
						 * .bodyString(join("[", joinedUsernames, "]"),
						 * ContentType.APPLICATION_JSON) .execute() .returnResponse();
						 */
						String deleteResponseContent = IOUtils.readAsString(deleteResponse.getEntity().getContent());
						System.out.println(deleteResponseContent);
						index += 100;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class IMGroupVO implements Serializable {

		private static final long serialVersionUID = 7328010047394317820L;
		@JsonInclude(Include.NON_NULL)
		private Long studentId;
		
		@JsonProperty("gid")
		@JsonInclude(Include.NON_NULL)
		private String groupId;
		
		@JsonInclude(Include.NON_NULL)
		@JsonProperty("owner_username")
		private String owner;
		
		@JsonInclude(Include.NON_NULL)
		@JsonProperty("name")
		private String groupName;
		
		@JsonInclude(Include.NON_EMPTY)
		@JsonProperty("members_username")
		private List<String> members = new ArrayList<>();
		
		@JsonInclude(Include.NON_NULL)
		private String avatar;
		
		@JsonInclude(Include.NON_NULL)
		private int flag;
		
		@JsonInclude(Include.NON_NULL)
		private String desc;

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public List<String> getMembers() {
			return members;
		}

		public void setMembers(List<String> members) {
			this.members = members;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public Long getStudentId() {
			return studentId;
		}

		public void setStudentId(Long studentId) {
			this.studentId = studentId;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

	}

	@NotThreadSafe
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
		public static final String METHOD_NAME = "DELETE";

		public String getMethod() {
			return METHOD_NAME;
		}

		public HttpDeleteWithBody(final String uri) {
			super();
			setURI(URI.create(uri));
		}

		public HttpDeleteWithBody(final URI uri) {
			super();
			setURI(uri);
		}

		public HttpDeleteWithBody() {
			super();
		}
	}
}
