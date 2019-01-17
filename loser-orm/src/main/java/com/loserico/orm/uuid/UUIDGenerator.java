package com.loserico.orm.uuid;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

/**
 * UUID生成器，去掉UUID中的-
 * 使用示例：<p><pre>
 * 	@Id
 * 	@GeneratedValue(generator = "UUID")
 * 	@GenericGenerator(name = "UUID", strategy = "com.sexyuncle.jpa.uuid.UUIDGenerator")
 * 	@Column(name = "ID", updatable = false, nullable = false)
 * 	private String uid;
 * </pre><p>
 * Copyright: Copyright (c) 2018-01-22 16:28
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class UUIDGenerator extends org.hibernate.id.UUIDGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}