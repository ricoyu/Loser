-- 表连接操作

fruits = {"banana","orange","apple"}
-- 返回表中字符串连接后的结果
print("Concatenated string ",table.concat(fruits))

--用字符串连接
print("Concatenated string ",table.concat(fruits, ", "))

--基于索引连接 fruits
print("Concatenated string ",table.concat(fruits, ", ", 2, 3))

