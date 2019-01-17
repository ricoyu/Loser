if redis.call("EXISTS", KEYS[1]) == 1 then
    local payload = redis.call("GET", KEYS[1])
    return cjson.decode(payload)[ARGV[1]]
else
    return nil
end

--[[
运行：redis-cli -a deepdata$ --eval json-get.lua apple , type
不需要KEY个数， key和参数之间要有逗号
--]]