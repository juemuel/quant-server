package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;

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
    // 新增版本：支持 IndicatorData 和 Factor 数据
    boolean isBuySignal(List<IndexData> data,
                        List<IndicatorData> indicatorDatas,
                        Map<String, List<Float>> factorValues, int currentIndex);

    boolean isSellSignal(List<IndexData> data,
                         List<IndicatorData> indicatorDatas,
                         Map<String, List<Float>> factorValues, int currentIndex);

    boolean isBuySignal(List<IndexData> data,
                        List<IndicatorData> indicatorDatas,
                        Map<String, List<Float>> factorValues, int currentIndex,
                        Map<String, Object> signalParams);

    boolean isSellSignal(List<IndexData> data,
                         List<IndicatorData> indicatorDatas,
                         Map<String, List<Float>> factorValues, int currentIndex,
                         Map<String, Object> signalParams);

}
