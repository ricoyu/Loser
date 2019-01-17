#!/usr/local/bin/lua
print("Hello Lua!");

local tbl1={};
local tbl2 = {"rico", "vivi", "zaizai"};
a = {};
a["key"] = "value";
key = 10;
a[key] = 22;
a[key] = a[key] + 11;
for k, v in pairs(a) do
	print(k .. " : "..v);
end
