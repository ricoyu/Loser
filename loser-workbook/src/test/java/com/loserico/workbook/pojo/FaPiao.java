package com.loserico.workbook.pojo;

import com.loserico.workbook.annotation.Col;

import lombok.Data;

@Data
public class FaPiao {

	@Col(name = "发票号")
	private String faPiao;
}
