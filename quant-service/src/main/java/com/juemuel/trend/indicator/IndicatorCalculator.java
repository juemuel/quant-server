package com.juemuel.trend.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;

import java.util.List;
import java.util.Map;

public interface IndicatorCalculator {
    String getName();
    Object calculate(List<IndexData> data, List<Trade> trades, Map<String, Object> params);
}
