--[[
实现功能：单点登录,指定时间内token自动过期
这里验证token有效性(存在与否，是否过期)
4个map
  auth:token:username       field是token，value是username
  auth:username:token       field是username，value是token 
  auth:token:userdetails    field是token，value是userdetails
  auth:token:authorities    field是token，value是authorities
  
1个zset  
  auth:token:ttl            放token即token到期timestamp,zset类型，score是timestamp
  
- 根据提供的token从auth:token:username中取username，取不到则验证失败
- 根据提供的token从auth:token:ttl获取score，与当前timestamp比较，超过有效时间范围执行登出操作清理数据,返回"expired"
- 返回username
]]