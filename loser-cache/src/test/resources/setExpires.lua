local MILISECONDS_A_YEAR = 31536000000;

--设置token过期时间
local setExpires = function(token, expires)
  -- 默认一年过期
  -- 当前毫秒数+过期毫秒数
  local expiresInMiliSeconds = expires
  if expiresInMiliSeconds == -1 then 
    --[[
          语法: ZADD key [NX|XX] [CH] [INCR] score member [score member ...]
         XX: Only update elements that already exist. Never add elements.
         NX: Don't update already existing elements. Always add new elements.
          
    Adds all the specified members with the specified scores to the sorted set stored at key. 
    It is possible to specify multiple score / member pairs. If a specified member is already 
    a member of the sorted set, the score is updated and the element reinserted at the right 
    position to ensure the correct ordering.
    --]]
    redis.call("ZADD", AUTH_TOKEN_TTL_ZSET, redis.call("TIME")[1] + MILISECONDS_A_YEAR, token)
  else
    redis.call("ZADD", AUTH_TOKEN_TTL_ZSET, redis.call("TIME")[1] + expiresInMiliSeconds, token)
  end
end