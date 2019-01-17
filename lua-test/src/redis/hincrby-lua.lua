--[[
1. HEXISTS key field
  查看哈希表 key 中，给定域 field 是否存在。

2. HINCRBY key field increment
  为哈希表 key 中的域 field 的值加上增量 increment 。
  增量也可以为负数，相当于对给定域进行减法操作。
  如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
  如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
  对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。

3. HSET key field value
  将哈希表 key 中的域 field 的值设为 value 。
  如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
  如果域 field 已经存在于哈希表中，旧值将被覆盖。
  
4. HDEL key field [field ...]
删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
--]]

if redis.call("HEXISTS", KEYS[1], ARGV[1]) == 1 then
  return redis.call("HINCRBY", KEYS[1], ARGV[1], 1)
else
  -- return nil
  return redis.call("HSET", KEYS[1], ARGV[1], 0)
end