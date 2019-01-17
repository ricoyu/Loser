package com.loserico.easemob;

import static java.text.MessageFormat.format;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import com.loserico.commons.jsonpath.JsonPathUtils;
import com.loserico.io.utils.IOUtils;

public class ChatMessageTest {

	@Test
	public void testRequestChatMessage() {
		String url = "https://a1.easemob.com/1117170727115727/pims/chatmessages/2018031314";
		LocalDateTime begin = LocalDateTime.of(2018, 3, 12, 07, 00);
		LocalDateTime end = LocalDateTime.now();
		
		LocalDateTime oneHourAgo = end.minusHours(1);
		while (oneHourAgo.isAfter(begin)) {
			try {
				String requestUrl = format(url, oneHourAgo.format(ofPattern("yyyyMMddHH")));
				String response = Request.Get(requestUrl)
					.addHeader("Authorization", "Bearer YWMtKr7JWicyEeiMhVELpo7lhAAAAAAAAAAAAAAAAAAAAAFV5SRgczkR55SxCZJ8bzFRAgMAAAFiImiiIwBPGgAwoD8Pho1UEBFX99JYweBKCcHFgXkEmRF8ESxgmygmJw")
					.addHeader("Content-Type", "application/json")
					.execute()
					.returnContent()
					.asString();
				if(!JsonPathUtils.ifExists(response, "$error")) {
					List<String> downloadUrls = JsonPathUtils.readListNode(response, "$.data[*].url", String.class);
					downloadUrls.forEach((downloadUrl) -> {
						System.out.println(downloadUrl);
						IOUtils.write("D://chatMessage.txt", downloadUrl);
					});
				} else {
					System.out.println("Chat message storage not exist");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testReadUrls() {
		String response = IOUtils.readClassPathFile("easemobChatMessage.json");
		List<String> downloadUrls = JsonPathUtils.readListNode(response, "$.data[*].url", String.class);
		downloadUrls.forEach(System.out::println);
	}
}
