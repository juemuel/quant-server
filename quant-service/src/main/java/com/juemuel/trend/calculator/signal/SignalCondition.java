package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import java.util.List;

/**
 * 交易信号判断接口
 */
public interface SignalCondition {
    boolean isBuySignal(List<IndexData> data, int currentIndex);
    boolean isSellSignal(List<IndexData> data, int currentIndex);
}
