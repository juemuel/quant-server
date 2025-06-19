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
    private long holdDays; // 持仓天数
    private float profitRate; // 实际收益率
    private float maxDrawdown; // 最大回撤
    private float sellReason; // 卖出原因（止损、止盈、信号触发）
}