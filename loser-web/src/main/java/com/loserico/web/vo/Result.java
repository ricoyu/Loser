package com.loserico.web.vo;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

public class Result {

	//请求接口状态码
	private int status = 200;

	/*
	 * 数据请求状态码 
	 * 0	代表成功
	 * 其他代表不同的错误码
	 */
	private int code = 0;

	/*
	 * message表示在API调用失败的情况下详细的错误信息，这个信息可以由客户端直接呈现给用户，否则为OK；
	 */
	private Object message = "OK";
	
	private Object debugMessage;

	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return toJson(this);
	}

	public Object getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(Object debugMessage) {
		this.debugMessage = debugMessage;
	}

}
