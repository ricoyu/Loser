##Available libraries
__The Redis Lua interpreter loads the following Lua libraries:__  

- base lib.  
- table lib.  
- string lib.  
- math lib.  
- struct lib.  
- cjson lib.  
- cmsgpack lib.  
- bitop lib.  
- redis.sha1hex function.  
- redis.breakpoint and redis.debug function in the context of the Redis Lua debugger.  

##Redis客户端运行lua脚本
>redis-cli -a deepdata$ --eval hello.lua

hello.lua内容

```

local msg = "Hello Lua"  
print(msg)  
return msg

```
  
  
  

