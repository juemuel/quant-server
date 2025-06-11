package com.juemuel.trend.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 每日资产价值 + 收益率
 */
@Data
public class Profit implements Serializable {
    private String date;
    private float value; // 资产价值
    private float rate;  // 收益率（相对于上一次交易时）
    private float cumulativeRate; // 累计收益率（与本金相比）
}