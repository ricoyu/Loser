print("10" + 1)
print("10 + 1")
--print("hello" + 1) -- ERROR (cannot convert "hello")

--[[
Lua applies such coercions not only in arithmetic operators, but also in other
places that expect a number, such as the argument to math.sin.
]]

-- Conversely, whenever Lua finds a number where it expects a string, it converts the number to a string:
-- The .. is the string concatenation operator in Lua.
print(10 .. 20)

-- A comparison like 10=="10" is false, because 10 is a number and “10” is a string.
print(10 == "10")

--If you need to convert a string to a number explicitly, you can use the function tonumber, which returns nil if the string does not denote a proper number:
--[[
line = io.read()
n = tonumber(line)
if n == nil then
  error(line .. " is not a valid number")
else
  print(n * 2)  
end
]]

print(tostring(10) == "10")
print(10 .. "" == "10")