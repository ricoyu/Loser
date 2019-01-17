#!/usr/local/bin/lua
function anonymous(tab, func)
	for k, v in pairs(tab) do
		print(func(k, v));
	end
end
tab = {key1 = "val1", key2 = "val2"};
anonymous(tab, function(k, v)
	return k .." = ".. v;
	end
)
