--[[
The for statement has two variants: the numeric for and the generic for
A numeric for has the following syntax:
  for var = exp1, exp2, exp3 do
    <something>
  end
  
This loop will execute something for each value of var from exp1 to exp2, using
exp3 as the step to increment var. This third expression is optional; 
when absent, Lua assumes 1 as the step value.  
]]
for i=1, 100 do print(i) end
for i=100, 1, -10 do print(i) end

-- If you want a loop without an upper limit, you can use the constant math.huge:
for i = 1, math.huge do
  if (0.3*i^3 - 20*i^2 - 500 >= 0) then
    print(i)
    break
  end
end

--[[
the pairs of a table (pairs), the entries of a sequence (ipairs),
]]
t = {}
t[1] = "hello"
t["a"] = "你好"
t[2] = 2

for k, v in ipairs(t) do
  print(k, v)
end  

print("=============")
for k, v in pairs(t) do
  print(k, v)
end  