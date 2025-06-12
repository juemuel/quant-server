package com.juemuel.trend.calculator.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import com.juemuel.trend.pojo.Trade;

import java.util.List;
import java.util.Map;

/**
 * 指标计算器
 */
public interface IndicatorCalculator {
    String getName();
    List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas);
}
