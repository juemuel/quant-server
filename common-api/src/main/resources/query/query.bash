set GROUP_ID=1
set ITEM_ID=1
set USER_ID=1

REM 创建分组
curl -X POST http://localhost:8031/api/common-api/group/create ^
-H "Content-Type: application/json" ^
-d "{\"typeCode\": \"stock\", \"name\": \"我的自选股\", \"ownerId\": %USER_ID%}"

REM 获取分组列表
curl -X GET "http://localhost:8031/api/common-api/group/list?userId=%USER_ID%&typeCode=stock"

REM 添加元素
curl -X POST http://localhost:8031/api/common-api/group/item/add ^
-H "Content-Type: application/json" ^
-d "{\"groupId\": %GROUP_ID%, \"itemName\": \"阿里巴巴\", \"customData\": {\"code\": \"09988\", \"market\": \"hk\"}, \"ownerId\": %USER_ID%}"

REM 搜索元素
curl -X GET "http://localhost:8031/api/common-api/group/item/search?groupId=%GROUP_ID%&keyword=阿里"