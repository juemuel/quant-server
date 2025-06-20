package com.juemuel.trend.pojo;

import lombok.Data;

/**
 * Tips: Float和float的区别
 */
@Data
public class IndicatorData {
    private IndexData indexData;
    // ma指标
    private Float ma;       // 移动平均线
    // ema指标
    private Float ema;      // 指数移动平均线
    // rsi指标
    private Float rsi;      // 相对强弱指数
    // macd指标
    private Float macd;     // MACD
    // 布林带指标
    private Float bollUpper; // 布林带上轨
    private Float bollMiddle;// 布林带中轨
    private Float bollLower; // 布林带下轨
    // obv指标
    private Float obv;
}
