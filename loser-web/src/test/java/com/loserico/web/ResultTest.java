package com.loserico.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.orm.bean.Page;
import com.loserico.web.vo.Result;
import com.loserico.web.vo.Results;

public class ResultTest {

	@Test
	public void testResults() {
		Result result = Results.builder()
				.success()
				.message("成功了")
				.result("真的成功了")
				.build();
		System.out.println(JacksonUtils.toJson(result));

		List<User> users = new ArrayList<User>();
		users.add(new User(1, "rico"));
		users.add(new User(2, "vivi"));

		result = Results.builder()
				.success()
				.message("成功了")
				.results(users)
				.build();
		System.out.println(JacksonUtils.toJson(result));

		Page page = new Page();
		page.setTotalPages(10);
		page.setPageSize(20);
		result = Results.builder()
				.success()
				.message("成功了")
				.results(users)
				.page(page)
				.build();
		System.out.println(JacksonUtils.toJson(result));
	}
}
