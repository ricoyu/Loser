package com.loserico.search.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.search.enums.OPType;

/**
 * Get 操作返回封装
 * <p>
 * Copyright: Copyright (c) 2018-08-21 09:32
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class GetResponse implements Response{
	
	private static final Logger logger = LoggerFactory.getLogger(GetResponse.class);

	private boolean exists; //对应文档是否存在

	private boolean isSourceEmpty;

	private String index;

	private String type;

	private String id;

	private long version;

	private String source;

	private byte[] byteSource;
	
	private org.elasticsearch.action.get.GetResponse getResponse;

	public GetResponse() {
	}

	public GetResponse(org.elasticsearch.action.get.GetResponse getResponse) {
		this.exists = getResponse.isExists();
		this.isSourceEmpty = getResponse.isSourceEmpty();
		this.index = getResponse.getIndex();
		this.type = getResponse.getType();
		this.id = getResponse.getId();
		this.version = getResponse.getVersion();
		this.source = getResponse.getSourceAsString();
		this.byteSource = getResponse.getSourceAsBytes();
		this.getResponse = getResponse;
	}

	/**
	 * 将source转成POJO对象
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> T toObject(Class<T> clazz) {
		if (!isSourceEmpty) {
			return JacksonUtils.toObject(source, clazz);
		}
		logger.warn("文档不存在! Index[{}], Type[{}], Id[{}]", index, type, id);
		return null;
	}

	public boolean isExists() {
		return exists;
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

	public boolean isSourceEmpty() {
		return isSourceEmpty;
	}

	public String getSource() {
		return source;
	}

	public byte[] getByteSource() {
		return byteSource;
	}
	
	@Override
	public String toString() {
		return getResponse.toString();
	}

	@Override
	public OPType operateType() {
		return OPType.GET;
	}

}
