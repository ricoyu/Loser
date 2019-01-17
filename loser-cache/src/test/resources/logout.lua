--[[
实现功能：单点登录,指定时间内token自动过期
这里实现登出
4个map
  auth:token:username       field是token，value是username
  auth:username:token       field是username，value是token 
  auth:token:userdetails    field是token，value是userdetails
  auth:token:authorities    field是token，value是authorities
  
1个zset  
  auth:token:ttl            放token即token到期timestamp,zset类型，score是timestamp
  
- 根据token从auth:token:username获取username
- 根据username删auth:username:token
- 根据token删auth:token:userdetails
- 根据token删auth:token:authorities
- 根据token删auth:token:ttl
]]