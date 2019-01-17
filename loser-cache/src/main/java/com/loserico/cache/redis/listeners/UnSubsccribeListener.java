package com.loserico.cache.redis.listeners;

public interface UnSubsccribeListener {

	public void onUnsubscribe(String channel, int subscribedChannels);
}
