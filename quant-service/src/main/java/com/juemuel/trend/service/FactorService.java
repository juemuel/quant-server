package com.juemuel.trend.service;

import com.juemuel.trend.calculator.factor.Factor;
import com.juemuel.trend.context.FactorContext;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FactorService {

    private final FactorContext factorContext;

    public FactorService(FactorContext factorContext) {
        this.factorContext = factorContext;
    }

    /**
     * 批量提取因子
     */
    public Map<String, List<Float>> extractFactors(
            List<IndexData> indexDatas,
            List<IndicatorData> indicatorDatas) {
        Map<String, List<Float>> result = new HashMap<>();

        for (Factor factor : factorContext.getAll()) {
            result.put(factor.getName(), factor.extract(indexDatas, indicatorDatas));
        }

        return result;
    }

    /**
     * 获取指定因子
     */
    public List<Float> getFactorValues(String name, List<IndexData> indexDatas, List<IndicatorData> indicatorDatas) {
        Factor factor = factorContext.get(name);
        if (factor == null) return Collections.emptyList();
        return factor.extract(indexDatas, indicatorDatas);
    }
}
