--[[
We can delimit literal strings also by matching double square brackets, as we do with long comments. 
Literals in this bracketed form can run for several lines and do not interpret escape sequences. 
Moreover, this form ignores the first character of the string when this character is a newline. 
This form is especially convenient for writing strings that contain large pieces of code, as in the following example
]]
page = [[
<html>
  <head>
    <title>An HTML Page</title>
  </head>
  <body>
    <a href="http://www.lua.org">Lua</a>
  </body>
</html>
]]
print(page)