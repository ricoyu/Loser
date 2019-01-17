--[[
调用方式：EVAL(script, 0, operate, username, token, expires, userDetails, authorities)

实现功能：单点登录,指定时间内token自动过期
这里实现登录
4个map
  auth:token:username       field是token，value是username
  auth:username:token       field是username，value是token 
  auth:token:userdetails    field是token，value是userdetails
  auth:token:authorities    field是token，value是authorities
  auth:token:login:info     field是token，value是额外的登录信息，如设备号、手机操作系统，IP地址等等。使用场景如app登出提示
  
1个zset  
  auth:token:ttl            放token即token到期timestamp,zset类型，score是timestamp
]]

local AUTH_USERNAME_TOKEN_HASH = "auth:username:token"
local AUTH_TOKEN_USERNAME_HASH = "auth:token:username"
local AUTH_TOKEN_USERDETAILS_HASH = "auth:token:userdetails"
local AUTH_TOKEN_AUTHORITIES_HASH = "auth:token:authorities"
local AUTH_TOKEN_LOGIN_INFO_HASH = "auth:token:login:info"
local AUTH_TOKEN_TTL_ZSET = "auth:token:ttl"
local AUTH_TOKEN_EXPIRE_CHANNEL = "auth:token:expired"

local OPERATE_LOGIN = "login"
local OPERATE_LOGOUT = "logout"
local OPERATE_AUTH = "auth"
local OPERATE_CLEAR_EXPIRED = "clearExpired"

--[[
1.登录成功后
客户端生成了新的token
- 根据username从auth:username:token获取之前用过的token
- 根据旧token删除auth:token:username中相应的field
- 根据旧token删除auth:token:userdetails中相应的field
- 根据旧token删除auth:token:authorities中相应的field
- 根据旧token从auth:token:ttl中删除
清理完毕

- 用新token设置auth:token:username
- 用新token设置auth:username:token
- 用新token设置auth:token:userdetails
- 用新token设置auth:token:authorities
- 将新token塞入auth:token:ttl，score为token过期时间
]]
local login = function(username, token, expires, userDetails, authorities, loginInfo)
  local loginResult = {}
  
  if(not username or not token) then
    -- table.insert(loginResult, false)
    loginResult["success"] = false
    return loginResult
  else
    loginResult["success"] = true
    --table.insert(loginResult, true)
  end
  local oldToken = redis.call("HGET", AUTH_USERNAME_TOKEN_HASH, username)
  
  redis.replicate_commands()
  if(oldToken) then
    local lastLoginInfo = redis.call("HGET", AUTH_TOKEN_LOGIN_INFO_HASH, oldToken)
    redis.call("HDEL", AUTH_USERNAME_TOKEN_HASH, username)
    redis.call("HDEL", AUTH_TOKEN_USERNAME_HASH, oldToken)
    redis.call("HDEL", AUTH_TOKEN_USERDETAILS_HASH, oldToken)
    redis.call("HDEL", AUTH_TOKEN_AUTHORITIES_HASH, oldToken)
    redis.call("HDEL", AUTH_TOKEN_LOGIN_INFO_HASH, oldToken)
    redis.call("ZREM", AUTH_TOKEN_TTL_ZSET, oldToken)
    loginResult["lastLoginInfo"] = lastLoginInfo;
    --table.insert(loginResult, lastLoginInfo)
  end
  
  redis.call("HSET", AUTH_USERNAME_TOKEN_HASH, username, token)
  redis.call("HSET", AUTH_TOKEN_USERNAME_HASH, token, username)
  redis.call("HSET", AUTH_TOKEN_USERDETAILS_HASH, token, userDetails)
  redis.call("HSET", AUTH_TOKEN_AUTHORITIES_HASH, token, authorities)
  redis.call("HSET", AUTH_TOKEN_LOGIN_INFO_HASH, token, loginInfo)
  
  -- 默认一年过期
  -- 当前毫秒数+过期毫秒数
  if expires == "-1" then 
    redis.call("ZADD", AUTH_TOKEN_TTL_ZSET, redis.call("TIME")[1] + 31536000000, token)
   else
     redis.call("ZADD", AUTH_TOKEN_TTL_ZSET, redis.call("TIME")[1] + expires, token)
   end
  
 return cjson.encode(loginResult)
end

--[[
2.登出 返回1表示登出成功 0表示token不存在
- 根据token从auth:token:username获取username
- 根据username删auth:username:token
- 根据token删auth:token:userdetails
- 根据token删auth:token:authorities
- 根据token删auth:token:ttl
]]
local logout = function(token) 
  local username = redis.call("HGET", AUTH_TOKEN_USERNAME_HASH, token)
  if(not username) then
    return cjson.encode(false)
  end
  redis.call("HDEL", AUTH_USERNAME_TOKEN_HASH, username)
  redis.call("HDEL", AUTH_TOKEN_USERNAME_HASH, token)
  redis.call("HDEL", AUTH_TOKEN_USERDETAILS_HASH, token)
  redis.call("HDEL", AUTH_TOKEN_AUTHORITIES_HASH, token)
  redis.call("HDEL", AUTH_TOKEN_LOGIN_INFO_HASH, token)
  redis.call("ZREM", AUTH_TOKEN_TTL_ZSET, token)
  return cjson.encode(true)
end

--[[
   根据auth:token:ttl的score，与当前timestamp比较，score<timestamp表示过期了, 超过有效时间范围执行登出操作清理数据,返回expiredTokenLoginInfo
   返回一个数组[token, loginInfo, token2, loginInfo2...]
   
 PUBLISH 消息
 ]]
local clearExpired = function()
  local currentTimestamp = redis.call("TIME")[1] -- 得到的是秒
  local expiredTokens = redis.call("ZRANGEBYSCORE", AUTH_TOKEN_TTL_ZSET, "-inf", currentTimestamp)

  local expiredTokenLoginInfo = {}
  -- 没有token过期
  if(#expiredTokens == 0) then
    return expiredTokenLoginInfo
  end

  redis.replicate_commands()
  for key, token in ipairs(expiredTokens) do
    -- loginInfo 是json字符串
    local loginInfo = redis.call("HGET", AUTH_TOKEN_LOGIN_INFO_HASH, token)
    return token
    --logout(token)
    --expiredTokenLoginInfo[token] = loginInfo
  end
  
  -- PUBLISH 只接受数字或者字符串，expiredTokenLoginInfo是table，所以转成json字符串
  local expiredTokenInfos = cjson.encode(expiredTokenLoginInfo);
  redis.call("PUBLISH", AUTH_TOKEN_EXPIRE_CHANNEL, expiredTokenInfos)
  return expiredTokenInfos
end

--[[
3.验证token
- 根据提供的token从auth:token:username中取username，取不到则验证失败
- 返回username
]]
local auth = function(token)
  clearExpired() --已经过期的token列表
  return redis.call("HGET", AUTH_TOKEN_USERNAME_HASH, token)
end

local operate = ARGV[1]
if operate == OPERATE_LOGIN then
  local username = ARGV[2] 
  local token = ARGV[3] 
  local expires = ARGV[4] 
  local userDetails = ARGV[5] 
  local authorities = ARGV[6]
  local loginInfo = ARGV[7]
  -- return username .. ", "..token .. ", "..expires .. ", "..userDetails ..", ".. authorities
  --return login(username, token, expires, userDetails, authorities, loginInfo)
elseif operate == OPERATE_LOGOUT then
  local token = ARGV[2] 
  return logout(token)
elseif operate == OPERATE_AUTH then
  local token = ARGV[2]
  return auth(token)
elseif operate == OPERATE_CLEAR_EXPIRED then
  return clearExpired()
end