print(type("Hello world"))
print(type(10.4*3))
print(type(1))
print(type(print))
print(type(true))
print(type(nil))
print(type(type(X)))

print(type(a)) -- nil ('a' is not initialized)
a = 10
print(type(a)) -- number
a = "a is a tring"
print(type(a)) -- string
a = print -- yes, this is valid!
print(type(a)) --function