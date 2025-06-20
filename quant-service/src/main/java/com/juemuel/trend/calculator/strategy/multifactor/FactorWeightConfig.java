package com.juemuel.trend.calculator.strategy.multifactor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "strategy.factor-weights")
public class FactorWeightConfig {
    private Map<String, Float> weights;

    public Map<String, Float> getWeights() {
        return weights;
    }

    public void setWeights(Map<String, Float> weights) {
        this.weights = weights;
    }
}
