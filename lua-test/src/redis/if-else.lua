local tables myArray = {"redis", "jedis", true, 88.0}

for i=1, #myArray
do
  if(myArray[i] == 'jedis')
  then
    print("true")
  else
    -- do nothing
  end
end