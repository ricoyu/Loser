a = "one string"
b = string.gsub(a, "one", "another")
print(a)  -- one string
print(b)  -- another string

-- 获取字符串长度
a = "hello"
print(#a) --> 5
print(#"good\0bye") --> 8

-- We can delimit literal strings by matching single or double quotes:
a = "line"
b = 'another line'