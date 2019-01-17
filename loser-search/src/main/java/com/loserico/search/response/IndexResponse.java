package com.loserico.search.response;

import org.elasticsearch.action.DocWriteResponse;

import com.loserico.search.enums.OPType;

/**
 * Index操作的Response
 * <p>
 * Copyright: Copyright (c) 2018-08-23 09:24
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class IndexResponse implements Response{

	private org.elasticsearch.action.index.IndexResponse indexResponse;

	private boolean created;

	private boolean updated;

	private boolean deleted;

	//If there is a version conflict
	private boolean conflict;

	private String index;

	private String type;

	private String id;

	private String result;

	private long version;

	private long seqNo;

	private long primaryTerm;

	private String shards;

	public IndexResponse(org.elasticsearch.action.index.IndexResponse indexResponse) {
		this.indexResponse = indexResponse;
		this.created = indexResponse.getResult() == DocWriteResponse.Result.CREATED;
		this.updated = indexResponse.getResult() == DocWriteResponse.Result.UPDATED;
		this.deleted = indexResponse.getResult() == DocWriteResponse.Result.DELETED;
		this.index = indexResponse.getIndex();
		this.type = indexResponse.getType();
		this.id = indexResponse.getId();
		this.seqNo = indexResponse.getSeqNo();
		this.primaryTerm = indexResponse.getPrimaryTerm();
		this.version = indexResponse.getVersion();
		this.shards = indexResponse.getShardInfo().toString();
		this.result = indexResponse.getResult().toString();
	}

	public IndexResponse(boolean conflict) {
		this.conflict = conflict;
	}

	public IndexResponse() {

	}

	public org.elasticsearch.action.index.IndexResponse getIndexResponse() {
		return indexResponse;
	}

	public void setIndexResponse(org.elasticsearch.action.index.IndexResponse indexResponse) {
		this.indexResponse = indexResponse;
	}

	public boolean isCreated() {
		return created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public long getPrimaryTerm() {
		return primaryTerm;
	}

	public void setPrimaryTerm(long primaryTerm) {
		this.primaryTerm = primaryTerm;
	}

	public String getShards() {
		return shards;
	}

	public void setShards(String shards) {
		this.shards = shards;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}

	public boolean isConflict() {
		return conflict;
	}

	public void setConflict(boolean conflict) {
		this.conflict = conflict;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexResponse[");
		builder.append("index=").append(index);
		builder.append(",type=").append(type);
		builder.append(",id=").append(id);
		builder.append(",version=").append(version);
		builder.append(",result=").append(result);
		builder.append(",seqNo=").append(seqNo);
		builder.append(",primaryTerm=").append(primaryTerm);
		builder.append(",shards=").append(shards);
		return builder.append("]").toString();
	}

	@Override
	public OPType operateType() {
		return OPType.CREATE;
	}

}
