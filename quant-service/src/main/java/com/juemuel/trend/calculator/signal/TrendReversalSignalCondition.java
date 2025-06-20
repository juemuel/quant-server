package com.juemuel.trend.calculator.signal;

import cn.hutool.core.convert.Convert;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
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
        return false;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex, Map<String, Object> signalParams) {
        return false;
    }
    @Override
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                               Map<String, List<Float>> factorValues, int currentIndex) {
        if (currentIndex < 1 || currentIndex >= indicatorDatas.size()) return false;

        float buyRate = 1.05f; // 默认参数
        float maPeriod = 20f;  // 默认周期

        IndexData current = data.get(currentIndex);
        Float avg = indicatorDatas.get(currentIndex).getMa(); // 使用 IndicatorData 提供的 MA

        if (avg == null || avg <= 0) return false;

        float closePoint = current.getClosePoint();
        return closePoint > avg && closePoint / avg >= buyRate;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                                Map<String, List<Float>> factorValues, int currentIndex) {
        if (currentIndex < 1 || currentIndex >= indicatorDatas.size()) return false;

        float sellRate = 0.95f; // 默认参数
        float maPeriod = 20f;

        IndexData current = data.get(currentIndex);
        Float avg = indicatorDatas.get(currentIndex).getMa();

        if (avg == null || avg <= 0) return false;

        float closePoint = current.getClosePoint();
        return closePoint < avg && closePoint / avg <= sellRate;
    }

    @Override
    public boolean isBuySignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                               Map<String, List<Float>> factorValues, int currentIndex,
                               Map<String, Object> signalParams) {
        float buyRate = Convert.toFloat(signalParams.getOrDefault("buyRate", "1.05"));
        float ma = Convert.toFloat(signalParams.getOrDefault("ma", "20"));

        IndexData current = data.get(currentIndex);
        Float avg = indicatorDatas.get(currentIndex).getMa(); // 使用 IndicatorData 提供的 MA

        if (avg == null || avg <= 0) return false;

        float closePoint = current.getClosePoint();
        return closePoint > avg && closePoint / avg >= buyRate;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, List<IndicatorData> indicatorDatas,
                                Map<String, List<Float>> factorValues, int currentIndex,
                                Map<String, Object> signalParams) {
        float sellRate = Convert.toFloat(signalParams.getOrDefault("sellRate", "0.95"));
        float ma = Convert.toFloat(signalParams.getOrDefault("ma", "20"));

        IndexData current = data.get(currentIndex);
        Float avg = indicatorDatas.get(currentIndex).getMa();

        if (avg == null || avg <= 0) return false;

        float closePoint = current.getClosePoint();
        float max = IndexDataUtil.getIndexDataMax(currentIndex, (int) ma, data);

        return closePoint < max && closePoint / max <= sellRate;
    }
}
