package com.loserico.search.response;

import org.elasticsearch.action.DocWriteResponse.Result;

import com.loserico.search.enums.OPType;

/**
 * 删除操作返回封装
 * <p>
 * Copyright: Copyright (c) 2018-08-23 09:25
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class DeleteResponse implements Response{

	private boolean deleted;

	private String index;

	private String type;

	private String id;

	private long version;

	private Result result;

	private org.elasticsearch.action.delete.DeleteResponse deleteResponse;

	public DeleteResponse(org.elasticsearch.action.delete.DeleteResponse deleteResponse) {
		this.deleted = deleteResponse.getResult() == Result.DELETED;
		this.index = deleteResponse.getIndex();
		this.type = deleteResponse.getType();
		this.id = deleteResponse.getId();
		this.result = deleteResponse.getResult();
		this.version = deleteResponse.getVersion();
		this.deleteResponse = deleteResponse;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Result getResult() {
		return result;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getIndex() {
		return index;
	}

	public long getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return deleteResponse.toString();
	}

	@Override
	public OPType operateType() {
		return OPType.DELETE;
	}

}
