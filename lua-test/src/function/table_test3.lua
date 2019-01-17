#!/usr/local/bin/lua
a3 = {};
for i = 1, 10 do
	a3[i] = i;
end

a3["key"] = "value";
print(a3["key"]);
print(a3["none"]);
print(a3[1]);
