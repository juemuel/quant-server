package com.juemuel.trend.service;

import com.juemuel.trend.calculator.indicator.IndicatorCalculator;
import com.juemuel.trend.context.IndicatorContext;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndicatorService {

    private final IndicatorContext indicatorContext;

    public IndicatorService(IndicatorContext indicatorContext) {
        this.indicatorContext = indicatorContext;
    }

    public List<IndicatorData> calculateIndicators(List<IndexData> indexDatas) {
        List<IndicatorData> result = initializeIndicatorData(indexDatas);

        for (IndicatorCalculator calculator : indicatorContext.getAll()) {
            calculator.calculate(indexDatas, result);
        }

        return result;
    }

    private List<IndicatorData> initializeIndicatorData(List<IndexData> indexDatas) {
        List<IndicatorData> list = new ArrayList<>();
        for (IndexData data : indexDatas) {
            IndicatorData indicatorData = new IndicatorData();
            indicatorData.setIndexData(data);
            list.add(indicatorData);
        }
        return list;
    }
}
