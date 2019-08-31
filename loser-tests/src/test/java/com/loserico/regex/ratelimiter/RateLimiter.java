package com.loserico.regex.ratelimiter;

public interface RateLimiter {

	boolean isOverLimit();

	long currentQPS();

	boolean visit();
}
