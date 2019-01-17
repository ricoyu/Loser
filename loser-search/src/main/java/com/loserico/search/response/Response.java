package com.loserico.search.response;

import com.loserico.search.enums.OPType;

/**
 * Elasticsearch 响应封装
 * <p>
 * Copyright: Copyright (c) 2018-08-24 13:52
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public interface Response {

	/**
	 * 返回该response对应的操作类型
	 * @return
	 */
	public OPType operateType();
}
