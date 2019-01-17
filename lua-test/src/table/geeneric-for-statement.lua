days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"}

--[[
  Now you want to translate a name into its position in the week. You can search
  the table, looking for the given name. As you will learn soon, however, you
  seldom do searches in Lua. A more efficient approach is to build a reverse table,
  say revDays, which has the names as indices and the numbers as values. This
  table would look like this:
]]

revDays = {["Sunday"] = 1, ["Monday"] = 2,
           ["Tuesday"] = 3, ["Wednesday"] = 4,
           ["Thursday"] = 5, ["Friday"] = 6,
           ["Saturday"] =7 }
revDays["Nonday"] = 8           
-- Then, all you have to do to find the order of a name is to index this reverse table:
x = "Tuesday"
print(revDays[x])
print(revDays["Nonday"])