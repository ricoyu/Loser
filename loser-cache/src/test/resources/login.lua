--[[
实现功能：单点登录,指定时间内token自动过期
这里实现登录
4个map
  auth:token:username       field是token，value是username
  auth:username:token       field是username，value是token 
  auth:token:userdetails    field是token，value是userdetails
  auth:token:authorities    field是token，value是authorities
  
1个zset  
  auth:token:ttl            放token即token到期timestamp,zset类型，score是timestamp
  
1.登录成功后
客户端生成了新的token

- 根据username从auth:username:token获取之前用过的token
- 根据旧token从auth:token:ttl中删除
- 根据旧token删除auth:token:username中相应的field
- 根据旧token删除auth:token:userdetails中相应的field
- 根据旧token删除auth:token:authorities中相应的field
清理完毕

- 用新token设置auth:token:username
- 用新token设置auth:username:token
- 用新token设置auth:token:userdetails
- 用新token设置auth:token:authorities

2.登出
- 根据token从auth:token:username获取username
- 根据username删auth:username:token
- 根据token删auth:token:userdetails
- 根据token删auth:token:authorities
- 根据token删auth:token:ttl

3.验证token
- 根据提供的token从auth:token:username中取username，取不到则验证失败
- 根据提供的token从auth:token:ttl获取score，与当前timestamp比较，超过有效时间范围执行登出操作清理数据,返回"expired"
- 返回username
]]
