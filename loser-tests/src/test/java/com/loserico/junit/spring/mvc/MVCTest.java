package com.loserico.junit.spring.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-*xml" })
// 配置事务的回滚,对数据库的增删改都会回滚,便于测试用例的循环利用
@Transactional
@Rollback(true)
@WebAppConfiguration
public class MVCTest {
	// 记得配置log4j.properties ,的命令行输出水平是debug
	private static final Logger log = LoggerFactory.getLogger(MVCTest.class);

	protected MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

	@Before() // 这个方法在每个方法执行之前都会执行一遍
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build(); // 初始化MockMvc对象
	}

	@org.junit.Test
	public void getAllCategoryTest() throws Exception {
		String responseString = mockMvc.perform(get("/categories/getAllCategory") // 请求的url,请求的方法是get
				.contentType(MediaType.APPLICATION_FORM_URLENCODED) // 数据的格式
				.param("pcode", "root") // 添加参数
		).andExpect(status().isOk()) // 返回的状态是200
				.andDo(print()) // 打印出请求和相应的内容
				.andReturn()
				.getResponse()
				.getContentAsString(); // 将相应的数据转换为字符串
		log.info("--------返回的json = {}", responseString);
	}

}