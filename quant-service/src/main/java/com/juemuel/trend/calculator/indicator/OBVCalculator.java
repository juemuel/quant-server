package com.juemuel.trend.calculator.indicator;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("obv")
public class OBVCalculator implements IndicatorCalculator {

    @Override
    public String getName() {
        return "obv";
    }

    @Override
    public List<IndicatorData> calculate(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        float obv = 0f;

        for (int i = 0; i < indexDatas.size(); i++) {
            IndexData current = indexDatas.get(i);
            IndexData prev = i == 0 ? current : indexDatas.get(i - 1);

            if (current.getClosePoint() > prev.getClosePoint()) {
//                obv += current.getVolume(); // 上涨时 + 成交量
            } else if (current.getClosePoint() < prev.getClosePoint()) {
//                obv -= current.getVolume(); // 下跌时 - 成交量
            }
            // 平盘不变化

//            indicatorDatas.get(i).setObv(obv); // 需要你在 IndicatorData 添加 setObv(float)
        }

        return indicatorDatas;
    }
}
