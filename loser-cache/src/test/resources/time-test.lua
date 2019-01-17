local time = redis.call("TIME")
return time[1]