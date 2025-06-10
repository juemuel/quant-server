# ���÷����ַ
$SERVICE_URL = "http://localhost:8001/parseIndex"

# ����Ҫ���Ե������ַ��������ŷָ���
$INPUT = "000300,��֤50,��֤��ý,test123,fakeindex"

# ���� URL ����
$ENCODED_INPUT = [uri]::EscapeDataString($INPUT)

# �������� URL��ʹ�� -f ��ʽ��ƴ�ӣ��������ʧ�ܣ�
$REQUEST_URL = ("{0}?codesOrNames={1}" -f $SERVICE_URL, $ENCODED_INPUT)

Write-Host ""
Write-Host "=============================="
Write-Host "     �������󵽽ӿ�"
Write-Host "=============================="
Write-Host "�����ַ:"
Write-Host $REQUEST_URL
Write-Host ""

# ���� GET ����
try {
    $response = Invoke-WebRequest -Uri $REQUEST_URL -Method Get -Headers @{"Content-Type"="application/json"}
    Write-Host "��Ӧ״̬�룺" $response.StatusCode
    Write-Host "��Ӧ���ݣ�"
    Write-Host $response.Content
} catch {
    Write-Host "����ʧ�ܣ�" $_.Exception.Message
}

Write-Host ""
Write-Host "��������˳�..."
Read-Host
