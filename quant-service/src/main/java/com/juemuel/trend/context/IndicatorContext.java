package com.juemuel.trend.context;

import com.juemuel.trend.calculator.indicator.IndicatorCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: context上下文的实现

/**
 * 指标计算器的注册中心/上下文容器
 */
@Service
public class IndicatorContext {
    private final Map<String, IndicatorCalculator> indicatorCalculators;

    public IndicatorContext(Map<String, IndicatorCalculator> indicatorCalculators) {
        this.indicatorCalculators = indicatorCalculators;
        System.out.println("Loaded strategies: " + indicatorCalculators.keySet());
    }

    public IndicatorCalculator get(String name) {
        return indicatorCalculators.get(name);
    }

    public List<IndicatorCalculator> getAll() {
        return new ArrayList<>(indicatorCalculators.values());
    }
}
