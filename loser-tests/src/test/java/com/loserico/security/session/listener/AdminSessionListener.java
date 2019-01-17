package com.loserico.security.session.listener;

import static com.loserico.commons.utils.DateUtils.ISO_DATE_MILISECONDS;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.commons.utils.DateUtils;

public class AdminSessionListener extends SessionListenerAdapter {
	private static final Logger log = LoggerFactory.getLogger(AdminSessionListener.class);

	@Override
	public void onStart(Session session) {
		log.info(log("Session stopped!", session));
	}

	@Override
	public void onStop(Session session) {
		log.info(log("Session stopped!", session));
	}

	@Override
	public void onExpiration(Session session) {
		log.info(log("Session stopped!", session));
	}

	private String log(String message, Session session) {
		return MessageFormat.format("${0} id=${1}, host=${2}, lastAccessTime=${3}, startTimestamp=${4}", message, session.getId(),
				session.getHost(), DateUtils.format(session.getLastAccessTime(), ISO_DATE_MILISECONDS),
				DateUtils.format(session.getStartTimestamp(), ISO_DATE_MILISECONDS),
				DateUtils.format(new Date(session.getTimeout()), ISO_DATE_MILISECONDS));
	}

}
