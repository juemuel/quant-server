package com.juemuel.trend.calculator.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 布林带指标计算器
 */
@Component("bollinger_band")
public class BollingerBandCalculator implements IndicatorCalculator {

    @Override
    public String getName() {
        return "bollinger_band";
    }

    @Override
    public List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        int period = 20; // 默认周期为20
        float stdDevMultiplier = 2; // 标准差倍数，默认为2

        for (int i = 0; i < indexDatas.size(); i++) {
            if (i < period - 1) {
                // 不足周期数据不计算
                indicatorDatas.get(i).setBollUpper(0f);
                indicatorDatas.get(i).setBollMiddle(0f);
                indicatorDatas.get(i).setBollLower(0f);
                continue;
            }

            float sum = 0;
            for (int j = i - period + 1; j <= i; j++) {
                sum += indexDatas.get(j).getClosePoint();
            }

            float ma = sum / period;

            float variance = 0;
            for (int j = i - period + 1; j <= i; j++) {
                float diff = indexDatas.get(j).getClosePoint() - ma;
                variance += diff * diff;
            }

            float stdDev = (float) Math.sqrt(variance / period);

            float upper = ma + stdDev * stdDevMultiplier;
            float lower = ma - stdDev * stdDevMultiplier;

            indicatorDatas.get(i).setBollUpper(upper);
            indicatorDatas.get(i).setBollMiddle(ma);
            indicatorDatas.get(i).setBollLower(lower);
        }

        return indicatorDatas;
    }
}
