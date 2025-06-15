package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.util.IndexDataUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于趋势反转的交易信号判断器
 */
@Component("trend_reversal_signal")
public class TrendReversalSignalCondition implements SignalCondition {

    private float buyRate = 1.05f;  // 默认突破比例
    private float sellRate = 0.95f; // 默认跌破比例

    @Override
    public boolean isBuySignal(List<IndexData> data, int currentIndex) {
        if (currentIndex < 1) return false;

        IndexData current = data.get(currentIndex);
        float closePoint = current.getClosePoint();
        float avg = IndexDataUtil.getMA(currentIndex, 20, data);

        return closePoint > avg && closePoint / avg >= buyRate;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex) {
        if (currentIndex < 1) return false;

        IndexData current = data.get(currentIndex);
        float closePoint = current.getClosePoint();
        float max = IndexDataUtil.getIndexDataMax(currentIndex, 20, data);

        return closePoint < max && closePoint / max <= sellRate;
    }

    // 支持外部配置阈值
    public void setBuyRate(float buyRate) {
        this.buyRate = buyRate;
    }

    public void setSellRate(float sellRate) {
        this.sellRate = sellRate;
    }
}
