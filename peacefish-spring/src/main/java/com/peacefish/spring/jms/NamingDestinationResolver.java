package com.peacefish.spring.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 根据destinationName的命名规则确定发送到Queue或者Topic
 * <p>
 * Copyright: Copyright (c) 2018-07-05 10:51
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class NamingDestinationResolver extends DynamicDestinationResolver {

	/**
	 * Resolve the specified destination name as a dynamic destination.
	 * @param session the current JMS Session
	 * @param destinationName the name of the destination
	 * @param pubSubDomain {@code true} if the domain is pub-sub, {@code false} if P2P
	 * @return the JMS destination (either a topic or a queue)
	 * @throws javax.jms.JMSException if resolution failed
	 * @see #resolveTopic(javax.jms.Session, String)
	 * @see #resolveQueue(javax.jms.Session, String)
	 */
	@Override
	public Destination resolveDestinationName(@Nullable Session session, String destinationName, boolean pubSubDomain)
			throws JMSException {

		Assert.notNull(session, "Session must not be null");
		Assert.notNull(destinationName, "Destination name must not be null");
		if (destinationName.toLowerCase().endsWith("topic")) {
			return resolveTopic(session, destinationName);
		}
		else {
			return resolveQueue(session, destinationName);
		}
	}
}