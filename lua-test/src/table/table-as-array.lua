-- read 10 lines, storing them in a table
a = {}
for i = 1, 10 do
  a[i] = io.read()
end

--[[
Given that you can index a table with any value, you can start the indices of
an array with any number that pleases you. However, it is customary in Lua to
start arrays with one (and not with zero, as in C) and several facilities in Lua
stick to this convention.

Often, however, the length is implicit. Remember that any non-initialized
index results in nil; you can use this value as a sentinel to mark the end of the
list. For instance, after you read 10 lines into a list, it is easy to know that its
length is 10, because its numeric keys are 1; 2; : : : ; 10. This technique only works
when the list does not have holes, which are nil elements inside it. We call such
a list without holes a sequence.

For sequences, Lua offers the length operator ‘#’. It returns the last index, or
the length, of the sequence. For instance, you could print the lines read in the
last example with the following code:
]]

for i = 1, #a do
  print(a[i])
end

--[[
Because we can index a table with any type, when indexing a table we have
the same subtleties that arise in equality. Although we can index a table both
with the number 0 and with the string “0”, these two values are different and
therefore denote different entries in a table. Similarly, the strings “+1”, “01”,
and “1” all denote different entries. When in doubt about the actual types of
your indices, use an explicit conversion to be sure:
]]
i = 10; j = "10"; k = "+10"
a = {}
a[i] = "one value"
a[j] = "another value"
a[k] = "yet another value"
print(a[i]) --> one value
print(a[j]) --> another value
print(a[k]) --> yet another value
print(a[tonumber(j)]) --> one value
print(a[tonumber(k)]) --> one value