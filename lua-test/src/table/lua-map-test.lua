local arr = {"a", "b"}
table.insert(arr, 1, 2)

for key, value in ipairs(arr) do
  print(key, value)
end