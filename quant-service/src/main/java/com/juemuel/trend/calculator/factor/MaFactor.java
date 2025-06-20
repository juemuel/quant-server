package com.juemuel.trend.calculator.factor;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于收盘价与移动平均线的差值
 * close / ma - 1 > 0, 看涨
 * close / ma - 1 = 0，等于
 * close / ma - 1 < 0，看跌
 */
@Component("ma_factor")
public class MaFactor implements Factor {

    @Override
    public String getName() {
        return "ma_factor";
    }

    @Override
    public List<Float> extract(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        List<Float> factors = new ArrayList<>();
        for (int i = 0; i < indexDatas.size(); i++) {
            float close = indexDatas.get(i).getClosePoint();
            Float ma = indicatorDatas.get(i).getMa();
            if (ma == null || ma == 0f) {
                factors.add(0f); // 或者 add(null)，根据前端处理方式决定
            } else {
                factors.add(close / ma - 1);
            }
        }
        return factors;
    }
}
