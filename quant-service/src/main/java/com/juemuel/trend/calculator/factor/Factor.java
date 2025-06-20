package com.juemuel.trend.calculator.factor;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;

import java.util.List;

public interface Factor {
    String getName();
    List<Float> extract(List<IndexData> indexDatas, List<IndicatorData> indicatorDatas);
}
