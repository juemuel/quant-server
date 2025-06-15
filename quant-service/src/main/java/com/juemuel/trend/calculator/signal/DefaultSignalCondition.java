package com.juemuel.trend.calculator.signal;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import com.juemuel.trend.util.IndexDataUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 MA 和 RSI 的默认交易信号判断器
 */
@Component("default_signal")
public class DefaultSignalCondition implements SignalCondition {

    private float buyRsiThreshold = 30;   // RSI < 30 表示超卖
    private float sellRsiThreshold = 70;  // RSI > 70 表示超买

    @Override
    public boolean isBuySignal(List<IndexData> data, int currentIndex) {
        if (currentIndex < 1) return false;

        IndexData current = data.get(currentIndex);
        IndicatorData indicatorData = (IndicatorData) current;
        return indicatorData.getIndexData().getClosePoint() > indicatorData.getMa()
                && indicatorData.getRsi() < buyRsiThreshold;
    }

    @Override
    public boolean isSellSignal(List<IndexData> data, int currentIndex) {
        if (currentIndex < 1) return false;

        IndexData current = data.get(currentIndex);
        IndicatorData indicatorData = (IndicatorData) current;

        return indicatorData.getIndexData().getClosePoint() < indicatorData.getMa()
                && indicatorData.getRsi() > sellRsiThreshold;
    }

    /**
     * 基于趋势反转的交易信号判断器
     */
    @Component("trend_reversal_signal")
    public static class TrendReversalSignalCondition implements SignalCondition {

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
}
