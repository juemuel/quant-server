package com.juemuel.trend.pojo;

import cn.hutool.core.convert.Convert;
import java.util.HashMap;
import java.util.Map;

public class StrategyParams {
    private String code;
    private int ma;
    private float buyThreshold;
    private float sellThreshold;
    private float serviceCharge;
    private String strStartDate;
    private String strEndDate;

    // 可选参数：用于支持不同策略的扩展参数
    private Map<String, Object> extraParams = new HashMap<>();

    public StrategyParams() {}

    public StrategyParams(String code, int ma, float buyThreshold, float sellThreshold,
                          float serviceCharge, String strStartDate, String strEndDate) {
        this.code = code;
        this.ma = ma;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.serviceCharge = serviceCharge;
        this.strStartDate = strStartDate;
        this.strEndDate = strEndDate;
    }

    // Getter and Setter
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getMa() { return ma; }
    public void setMa(int ma) { this.ma = ma; }

    public float getBuyThreshold() { return buyThreshold; }
    public void setBuyThreshold(float buyThreshold) { this.buyThreshold = buyThreshold; }

    public float getSellThreshold() { return sellThreshold; }
    public void setSellThreshold(float sellThreshold) { this.sellThreshold = sellThreshold; }

    public float getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(float serviceCharge) { this.serviceCharge = serviceCharge; }

    public String getStrStartDate() { return strStartDate; }
    public void setStrStartDate(String strStartDate) { this.strStartDate = strStartDate; }

    public String getStrEndDate() { return strEndDate; }
    public void setStrEndDate(String strEndDate) { this.strEndDate = strEndDate; }

    public Map<String, Object> getExtraParams() { return extraParams; }
    public void setExtraParams(Map<String, Object> extraParams) { this.extraParams = extraParams; }

    // 从 Map 构建 StrategyParams（适用于 @RequestParam 或 URL 参数）
    public static StrategyParams fromMap(Map<String, String> rawParams) {
        StrategyParams params = new StrategyParams();
        params.setCode(rawParams.get("code"));
        params.setMa(Convert.toInt(rawParams.get("ma")));
        params.setBuyThreshold(Convert.toFloat(rawParams.get("buyThreshold")));
        params.setSellThreshold(Convert.toFloat(rawParams.get("sellThreshold")));
        params.setServiceCharge(Convert.toFloat(rawParams.get("serviceCharge")));
        params.setStrStartDate(rawParams.get("startDate"));
        params.setStrEndDate(rawParams.get("endDate"));

        // 其他可选参数存入 extraParams
        for (Map.Entry<String, String> entry : rawParams.entrySet()) {
            if (!entry.getKey().matches("code|ma|buyThreshold|sellThreshold|serviceCharge|startDate|endDate")) {
                params.getExtraParams().put(entry.getKey(), entry.getValue()); // 先不转换，由策略自己决定如何解析
            }
        }
        return params;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("ma", ma);
        map.put("buyThreshold", buyThreshold);
        map.put("sellThreshold", sellThreshold);
        map.put("serviceCharge", serviceCharge);
        map.put("startDate", strStartDate);
        map.put("endDate", strEndDate);
        map.putAll(extraParams); // 添加扩展参数
        return map;
    }
}
