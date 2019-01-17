#!/usr/local/bin/lua
function maximum(a)
	local mi = 1;	-- 最大值索引
	local m = a[mi];	-- 最大值

	for i, val in ipairs(a) do
		if(val > m) then
			mi = i;
			m = val;
		end
	end

	return m, mi;
end
	
print(maximum({1, 6, 3, 12, 56, 4, 2, 90}));