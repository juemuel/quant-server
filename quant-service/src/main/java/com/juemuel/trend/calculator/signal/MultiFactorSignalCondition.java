package com.juemuel.trend.calculator.signal;

import cn.hutool.core.convert.Convert;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import com.juemuel.trend.util.IndexDataUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 多因子组合信号条件
 */
@Component("multi_factor_signal")
public class MultiFactorSignalCondition implements SignalCondition {

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
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                               Map<String, List<Float>> factorValues, int currentIndex) {
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                                Map<String, List<Float>> factorValues, int currentIndex) {
        return false;
    }

    @Override
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                               Map<String, List<Float>> factorValues, int currentIndex,
                               Map<String, Object> signalParams) {
        float maBuyRate = Convert.toFloat(signalParams.getOrDefault("maBuyRate", "1.05"));
        float rsiThreshold = Convert.toFloat(signalParams.getOrDefault("rsiThreshold", "30"));

        Float maFactor = factorValues.getOrDefault("ma_factor", Collections.emptyList()).get(currentIndex);
        Float rsiFactor = factorValues.getOrDefault("rsi_factor", Collections.emptyList()).get(currentIndex);

        return maFactor != null && rsiFactor != null && maFactor > 0 && rsiFactor < rsiThreshold;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                                Map<String, List<Float>> factorValues, int currentIndex,
                                Map<String, Object> signalParams) {
        float sellRate = Convert.toFloat(signalParams.getOrDefault("sellRate", "0.95"));

        IndexData current = data.get(currentIndex);
        Float avg = indicatorDatas.get(currentIndex).getMa();

        if (avg == null || avg <= 0) return false;

        float closePoint = current.getClosePoint();
        float max = IndexDataUtil.getIndexDataMax(currentIndex, 20, data);

        return closePoint < max && closePoint / max <= sellRate;
    }
}
