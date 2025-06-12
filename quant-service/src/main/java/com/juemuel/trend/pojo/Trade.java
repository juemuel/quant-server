package com.juemuel.trend.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Trade implements Serializable {
    private String buyDate;
    private String sellDate;
    private float buyClosePoint;
    private float sellClosePoint;
    private float rate; // 资金回报率
    //TODO：考虑补充
    private float profitRate; // 新增：交易回报率（点位差回报率）
    private long holdDays;    // 新增：持有天数
}