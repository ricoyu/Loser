local tables user_1 = {age = 28, name = "tome"}
print(user_1["age"])

for key, value in pairs(user_1)
do
  print(key ..": "..value)
end