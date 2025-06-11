package com.juemuel.trend.service;

import com.juemuel.trend.indicator.IndicatorCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: context上下文的实现
@Service
public class IndicatorContext {
    private final Map<String, IndicatorCalculator> calculators;

    public IndicatorContext(Map<String, IndicatorCalculator> calculators) {
        this.calculators = calculators;
    }

    public IndicatorCalculator get(String name) {
        return calculators.get(name);
    }

    public List<IndicatorCalculator> getAll() {
        return new ArrayList<>(calculators.values());
    }
}
