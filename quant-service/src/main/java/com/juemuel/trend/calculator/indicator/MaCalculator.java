package com.juemuel.trend.calculator.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import com.juemuel.trend.pojo.Trade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("ma")
public class MaCalculator implements IndicatorCalculator {
    @Override
    public String getName() {
        return "ma";
    }

    @Override
    public List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        int period = 20; // 可从配置或参数中读取
        for (int i = 0; i < indexDatas.size(); i++) {
            if (i < period) {
                indicatorDatas.get(i).setMa(0f); // 不足周期数不计算
            } else {
                float sum = 0;
                for (int j = i - period; j <= i; j++) {
                    sum += indexDatas.get(j).getClosePoint();
                }
                float ma = sum / period;
                indicatorDatas.get(i).setMa(ma);
            }
        }
        return indicatorDatas;
    }
}
