local a = {}
local name = "name"
a[name] = "rico"
redis.log(redis.LOG_NOTICE, a.name)
return a.name