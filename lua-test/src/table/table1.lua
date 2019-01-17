mytable={}
print("Type of mytable is ", type(mytable))

mytable[1]="Lua"
mytable["wow"]="Tutorial"

print("mytable Element at index 1 is ", mytable[1])
print("mytable Element at index wow is ", mytable["wow"])
print("mytable Element at index 2 is ", mytable[2])

for key, val in ipairs(mytable) do
  print("key "..key..", value "..val)
end

-- alternatetable 与 mytable 引用相同的表
alternatetable = mytable
print("alternatetable Element at index 1 is ", alternatetable[1])
print("mytable Element at index wow is ", alternatetable["wow"])

alternatetable["wow"] = "I changed it"
print("mytable Element at index wow is ", mytable["wow"])

-- 只是变量被释放，表本身没有被释放
alternatetable = nil
print("alternatetable is ", alternatetable)

-- mytable 仍然可以访问
print("mytable Element at index wow is ", mytable["wow"])

mytable = nil
print("mytable is ", mytable)