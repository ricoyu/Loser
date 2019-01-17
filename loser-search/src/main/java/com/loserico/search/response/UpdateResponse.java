package com.loserico.search.response;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.index.get.GetResult;

import com.loserico.search.enums.OPType;

/**
 * 更新操作的返回封装
 * <p>
 * Copyright: Copyright (c) 2018-08-23 09:27
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class UpdateResponse implements Response{

	private String index;

	private String type;

	private String id;

	private boolean created; //Handle the case where the document was created for the first time (upsert)

	private boolean updated; //Handle the case where the document was updated

	private boolean deleted; //Handle the case where the document was deleted
	
	private boolean notFound; //更新不存在的文档
	
	private boolean conflicted; //版本冲突

	private boolean noop; //Handle the case where the document was not impacted by the update, ie no operation (noop) was executed on the document

	private long version;

	private String source;

	private org.elasticsearch.action.update.UpdateResponse updateResponse;

	public UpdateResponse() {
	}

	public UpdateResponse(org.elasticsearch.action.update.UpdateResponse updateResponse) {
		this.index = updateResponse.getIndex();
		this.type = updateResponse.getType();
		this.id = updateResponse.getId();
		this.version = updateResponse.getVersion();

		if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
			this.created = true;
		} else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
			this.updated = true;
		} else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
			this.deleted = true;
		} else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
			this.noop = true;
		}

		/*
		 * When the source retrieval is enabled in the UpdateRequest through
		 * the fetchSource method, the response contains the source of the
		 * updated document:
		 */
		GetResult result = updateResponse.getGetResult();
		if (result.isExists()) {
			this.source = result.sourceAsString();
		}
	}

	@Override
	public OPType operateType() {
		return OPType.UPDATE;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public boolean isCreated() {
		return created;
	}

	public boolean isUpdated() {
		return updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isNotFound() {
		return notFound;
	}

	public void setNotFound(boolean notFound) {
		this.notFound = notFound;
	}

	public boolean isNoop() {
		return noop;
	}

	public String getSource() {
		return source;
	}

	public org.elasticsearch.action.update.UpdateResponse getUpdateResponse() {
		return updateResponse;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isConflicted() {
		return conflicted;
	}

	public void setConflicted(boolean conflicted) {
		this.conflicted = conflicted;
	}
}
