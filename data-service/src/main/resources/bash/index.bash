@echo off
echo 测试 Trend Data Service 接口...

echo.
echo 获取所有指数:
curl -X GET http://localhost:8001/getCodes

echo.
echo 刷新指数列表:
curl -X GET http://localhost:8001/freshCodes

echo.
echo 清除缓存:
curl -X GET http://localhost:8001/removeCodes

echo.
echo 解析指数（中文已编码，curl中无法自动编码，请自行编码）:
curl -X GET "http://localhost:8001/parseIndex?codesOrNames=000300,%E4%B8%8A%E8%AF%8150,%E6%B2%AA%E6%B7%B1300"

pause
