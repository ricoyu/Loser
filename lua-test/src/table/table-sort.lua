-- 表排序操作
fruits = {"banana","orange","apple","grapes"}

for k, v in ipairs(fruits) do
  print(k , v)
end

table.sort(fruits)
print("sorted table fruits")
for k, v in ipairs(fruits) do
  print(k , v)
end