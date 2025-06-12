package com.juemuel.trend.calculator.indicator;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import com.juemuel.trend.pojo.Trade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("rsi")
public class RsiCalculator implements IndicatorCalculator {
    @Override
    public String getName() {
        return "rsi";
    }

    @Override
    public List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        int period = 14;
        // 初始化 IndicatorData 列表
        for (IndexData data : indexDatas) {
            IndicatorData indicatorData = new IndicatorData();
            indicatorData.setIndexData(data);
            indicatorDatas.add(indicatorData);
        }

        float[] gains = new float[indexDatas.size()];
        float[] losses = new float[indexDatas.size()];

        for (int i = 1; i < indexDatas.size(); i++) {
            float diff = indexDatas.get(i).getClosePoint() - indexDatas.get(i - 1).getClosePoint();
            gains[i] = Math.max(diff, 0);
            losses[i] = Math.max(-diff, 0);
        }

        for (int i = period; i < indexDatas.size(); i++) {
            float gainSum = 0;
            float lossSum = 0;

            for (int j = i - period + 1; j <= i; j++) {
                gainSum += gains[j];
                lossSum += losses[j];
            }

            float avgGain = gainSum / period;
            float avgLoss = lossSum / period;

            if (avgLoss == 0) {
                indicatorDatas.get(i).setRsi(100f);
            } else {
                float rs = avgGain / avgLoss;
                float rsi = 100 - (100 / (1 + rs));
                indicatorDatas.get(i).setRsi(rsi);
            }
        }

        return indicatorDatas;
    }

}
