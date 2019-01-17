package com.loserico.cache.redis.listeners;

public interface MessageListener {

	void onMessage(String channel, String message);
}
