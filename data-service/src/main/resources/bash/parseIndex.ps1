# 设置服务地址
$SERVICE_URL = "http://localhost:8001/parseIndex"

# 定义要测试的输入字符串（逗号分隔）
$INPUT = "000300,上证50,中证传媒,test123,fakeindex"

# 进行 URL 编码
$ENCODED_INPUT = [uri]::EscapeDataString($INPUT)

# 构造完整 URL（使用 -f 格式化拼接，避免解析失败）
$REQUEST_URL = ("{0}?codesOrNames={1}" -f $SERVICE_URL, $ENCODED_INPUT)

Write-Host ""
Write-Host "=============================="
Write-Host "     发起请求到接口"
Write-Host "=============================="
Write-Host "请求地址:"
Write-Host $REQUEST_URL
Write-Host ""

# 发起 GET 请求
try {
    $response = Invoke-WebRequest -Uri $REQUEST_URL -Method Get -Headers @{"Content-Type"="application/json"}
    Write-Host "响应状态码：" $response.StatusCode
    Write-Host "响应内容："
    Write-Host $response.Content
} catch {
    Write-Host "请求失败：" $_.Exception.Message
}

Write-Host ""
Write-Host "按任意键退出..."
Read-Host
