local sum = 0
for i = 1, 100
do 
  sum = sum+ i
end

--5050
--print(sum)

local tables myArray = {"redis", "jedis", true, 88.0}
for i =1, #myArray
do
  --print(myArray[i])
end

--ipairs
for index, value in ipairs(myArray)
do
  print(index)
  print(value)
end