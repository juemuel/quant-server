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
# 更新分组
curl -X POST http://localhost:8031/api/common-api/group/update ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"typeCode\": \"stock\", \"name\": \"我的新股票\", \"ownerId\": 1}"

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
# 更新分组项
curl -X POST 'http://localhost:8031/api/common-api/group/item/update' \
-H 'Content-Type: application/json' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84' \
--data-raw '{
  "itemId": 1,
  "groupId": 1,
  "notes": "这是笔记",
  "itemName": "腾讯控股2",
  "customData": {
    "code": "00700",
    "market": "hk"
  },
  "ownerId": 1
}'

# 获取分组列表（含分组项）
curl -X GET "http://localhost:8031/api/common-api/group/list?userId=1&groupId=1001&keyword=腾讯" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" \




# 阿里巴巴
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"itemName\": \"阿里巴巴\", \"customData\": {\"code\": \"09988\", \"market\": \"hk\"}, \"ownerId\": 1}"

# 腾讯控股
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"itemName\": \"腾讯控股\", \"customData\": {\"code\": \"00700\", \"market\": \"hk\"}, \"ownerId\": 1}"

# 苹果公司
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 1, \"itemName\": \"苹果公司\", \"customData\": {\"code\": \"AAPL\", \"market\": \"NASDAQ\"}, \"ownerId\": 1}"

# 恒生指数
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 2, \"itemName\": \"恒生指数\", \"customData\": {\"code\": \"HSI\", \"market\": \"hk\"}, \"ownerId\": 1}"

# 标普500
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 2, \"itemName\": \"标普500\", \"customData\": {\"code\": \"SPX\", \"market\": \"US\"}, \"ownerId\": 1}"

# 日经225
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJOYW1lIjoibHZqdmppZSIsImV4cCI6MTc0ODM2MDUwMn0.R8ARQmDH4-_kCiUibE4vTICX__YJ1HZvVthRFOyIr84" ^
-d "{\"groupId\": 2, \"itemName\": \"日经225\", \"customData\": {\"code\": \"N225\", \"market\": \"JP\"}, \"ownerId\": 1}"
