-- 用法，注意KEY和传入参数之间的逗号, 分割了KEYS[]和ARGV[]
-- redis-cli -a deepdata$ --eval incrset.lua 2 links:counr links:url ,  http://malcolmgladwellbookgenerator.com/
--When calling EVAL, after the script we provide 2 as the number of KEYS that will be accessed, then we list our KEYS, and finally we provide values for ARGV.
local link_id = redis.call("incr", KEYS[1])
redis.call("hset", KEYS[2], link_id, ARGV[1])
return link_id
