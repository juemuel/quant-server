package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("custom_signal")
public class CustomSignalCondition implements SignalCondition {
    @Override
    public boolean isBuySignal(List<IndexData> data, int currentIndex) {
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex) {
        return false;
    }

    @Override
    public boolean isBuySignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams) {
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams) {
        return false;
    }

    @Override
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas, Map<String, List<Float>> factorValues, int currentIndex) {
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas, Map<String, List<Float>> factorValues, int currentIndex) {
        return false;
    }

    @Override
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas, Map<String, List<Float>> factorValues, int currentIndex, Map<String, Object> signalParams) {
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas, Map<String, List<Float>> factorValues, int currentIndex, Map<String, Object> signalParams) {
        return false;
    }
}
