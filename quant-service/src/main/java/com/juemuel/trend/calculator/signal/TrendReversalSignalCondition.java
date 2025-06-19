package com.juemuel.trend.calculator.signal;

import cn.hutool.core.convert.Convert;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.util.IndexDataUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 基于趋势反转的交易信号判断器
 */
@Component("trend_reversal_signal")
public class TrendReversalSignalCondition implements SignalCondition {
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
        if (currentIndex < 1) return false;
        float buyRate = Convert.toFloat(signalParams.getOrDefault("buyRate", "1.05"));
        float ma = (float) signalParams.getOrDefault("ma", 20f);

        IndexData current = data.get(currentIndex);
        float closePoint = current.getClosePoint();
        float avg = IndexDataUtil.getMA(currentIndex, (int) ma, data);

        return closePoint > avg && closePoint / avg >= buyRate;
    }
    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams) {
        if (currentIndex < 1) return false;
        float sellRate = Convert.toFloat(signalParams.getOrDefault("sellRate", 0.95f));
        float ma = (float) signalParams.getOrDefault("ma", 20f);

        IndexData current = data.get(currentIndex);
        float closePoint = current.getClosePoint();
        float max = IndexDataUtil.getIndexDataMax(currentIndex, (int) ma, data);

        return closePoint < max && closePoint / max <= sellRate;
    }
}
