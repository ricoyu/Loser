-- 插入与移出操作
fruits = {"banana","orange","apple"}

-- 在 fruits 的末尾插入一种水果
table.insert(fruits, "mongo")
print("Fruit at index 4 is ", fruits[4])
-- 在索引 2 的位置插入一种水果
table.insert(fruits, 2, "grapes")
print("Fruit at index 2 is ", fruits[2])

print("The maximum elements in table is", table.maxn(fruits))

print("The last element is",fruits[5])
print("The last element is",fruits[table.maxn(fruits)])

table.remove(fruits)
print("The previous last element is",fruits[5])