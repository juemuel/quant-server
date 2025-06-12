package com.juemuel.trend.calculator.trade;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;

import java.util.List;
import java.util.Map;

/**
 * 策略收益统计
 */
public interface TradeCalculator {
    String getName();
    Object calculate(List<IndexData> data, List<Trade> trades, Map<String, Object> params);
}
