package com.juemuel.trend.calculator.strategy;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;

import java.util.List;
import java.util.Map;

public interface TradingStrategy {
    String getName(); // 策略名称，用于选择
    List<Trade> execute(List<IndexData> indexDatas, Map<String, Object> params);
}
