package com.juemuel.trend.calculator.factor;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component("rsi_factor")
public class RsiFactor implements Factor {
    @Override
    public String getName() {
        return "rsi_factor";
    }

    @Override
    public List<Float> extract(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        List<Float> factors = new ArrayList<>();
        for (IndicatorData data : indicatorDatas) {
            Float rsi = data.getRsi();
            if (rsi == null) rsi = 50f;
            factors.add(rsi); // 直接返回 RSI 值
        }
        return factors;
    }
}
