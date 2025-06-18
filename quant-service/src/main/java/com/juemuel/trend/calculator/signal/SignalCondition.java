package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import java.util.List;
import java.util.Map;

/**
 * 交易信号判断接口
 */
public interface SignalCondition {
    boolean isBuySignal(List<IndexData> data, int currentIndex);
    boolean isSellSignal(List<IndexData> data, int currentIndex);
    boolean isBuySignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams);
    boolean isSellSignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams);
}
