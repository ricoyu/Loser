#!/usr/local/bin/lua
--[[ 函数返回两个值的最大值 --]]
function max(a, b)
	local result;
	if (a > b) then
		result = a;
	else
		result = b;
	end
	return result;
end

print(max(1,2));
print(max(4,3));