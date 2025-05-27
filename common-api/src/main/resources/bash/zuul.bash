@echo off
set TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84
set GROUP_ID=1
set USER_ID=1
# 登录
curl -X POST http://localhost:8031/api/common-api/login ^
-H "Content-Type: application/json" ^
-d "{\"userName\": \"lvjvjie\", \"passWord\": \"lvjvjie\"}"
# 创建分组
curl -X POST http://localhost:8031/api/common-api/group/create ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"typeCode\": \"stock\", \"name\": \"我的股票\", \"ownerId\": 1}"
curl -X POST http://localhost:8031/api/common-api/group/create ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"typeCode\": \"stock\", \"name\": \"我的指数\", \"ownerId\": 1}"
# 删除分组
curl -X POST http://localhost:8031/api/common-api/group/delete ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"ownerId\": 1}"

# 获取分组列表（含分组项）
curl -X GET "http://localhost:8031/api/common-api/group/list?userId=1&typeCode=stock" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84"

# 添加分组项到某个分组
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"itemName\": \"阿里巴巴\", \"customData\": {\"code\": \"09988\", \"market\": \"hk\"}, \"ownerId\": 1}"
# 删除分组项
curl -X POST http://localhost:8031/api/common-api/group/item/delete ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"itemId\": 1, \"ownerId\": 1}"
# 根据关键词搜索分组内元素
curl -X GET "http://localhost:8031/api/common-api/group/item/search?groupId=1&keyword=阿里" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^

