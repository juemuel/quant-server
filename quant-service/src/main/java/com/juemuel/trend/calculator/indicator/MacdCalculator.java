package com.juemuel.trend.calculator.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MACD 指标计算器（异步加载）
 */
@Component("macd")
public class MacdCalculator implements IndicatorCalculator {

    @Override
    public String getName() {
        return "macd";
    }

    @Override
    public List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        int shortPeriod = 12; // 快线周期
        int longPeriod = 26;  // 慢线周期
        int signalPeriod = 9; // 信号线周期

        float[] emaShort = new float[indexDatas.size()];
        float[] emaLong = new float[indexDatas.size()];
        float[] dif = new float[indexDatas.size()];
        float[] dea = new float[indexDatas.size()];
        float[] macd = new float[indexDatas.size()];

        // 计算 EMA（指数移动平均）
        emaShort[0] = indexDatas.get(0).getClosePoint();
        emaLong[0] = indexDatas.get(0).getClosePoint();

        for (int i = 1; i < indexDatas.size(); i++) {
            emaShort[i] = ema(indexDatas, i, shortPeriod, emaShort[i - 1]);
            emaLong[i] = ema(indexDatas, i, longPeriod, emaLong[i - 1]);

            dif[i] = emaShort[i] - emaLong[i];

            if (i >= signalPeriod) {
                dea[i] = ema(dea, i, signalPeriod, dea[i - 1]);
            } else {
                dea[i] = 0;
            }

            macd[i] = 2 * (dif[i] - dea[i]);
        }

        for (int i = 0; i < indexDatas.size(); i++) {
            indicatorDatas.get(i).setMacd(macd[i]);
        }

        return indicatorDatas;
    }

    /**
     * 计算 EMA（指数加权移动平均）
     */
    private float ema(List<IndexData> data, int currentIndex, int period, float lastEma) {
        float k = 2f / (period + 1);
        return k * data.get(currentIndex).getClosePoint() + (1 - k) * lastEma;
    }

    /**
     * 计算 EMA（用于数组中的历史值）
     */
    private float ema(float[] dataArray, int currentIndex, int period, float lastEma) {
        float k = 2f / (period + 1);
        return k * dataArray[currentIndex] + (1 - k) * lastEma;
    }
}
