package com.juemuel.trend.pojo;

import lombok.Data;

/**
 * 年度收益
 */
@Data
public class AnnualProfit {
    private int year;
    private float value; // 年度资产价值
    private float rate; // 相对于上一次交易时的收益率
    private float cumulativeRate; // 累计收益率（与本金相比）
}
