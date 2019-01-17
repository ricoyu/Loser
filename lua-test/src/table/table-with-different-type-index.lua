-- Each table can store values with different types of indices, and it grows as needed to accommodate new entries:

a = {} -- empty table
-- create 1000 new entries
for i = 1, 1000 do 
  a[i] = i*2
end

print(a[9])  

a["x"] = 10
print(a["x"])
print(a["y"])
print(a.x)
print(a.y)

--[[
common mistake for beginners is to confuse a.x with a[x]. The first form
represents a["x"], that is, a table indexed by the string “x”. The second form is
a table indexed by the value of the variable x. See the difference:
]]
a = {}
x = "y"
a[x] = 10   -- put 10 in field "y"
print(a[x]) -- value of field "y"
print(a.x)  -- value of field "x" (undefined)
print(a.y)  -- value of field "y"