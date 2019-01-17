package com.loserico.commons.utils;
public enum Status {
	DRAFT(0, "草稿", "status.draft"),
	PENDING_APPROVAL(1, "等待批准", "status.pending.approval"),
	CONFIRMED(2, "已确认", "status.confirmed"),
	REJECTED(3, "驳回", "status.rejected");

	private int code;
	private String desc;
	private String template; //国际化消息模版

	private Status(int code, String desc, String template) {
		this.code = code;
		this.desc = desc;
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}