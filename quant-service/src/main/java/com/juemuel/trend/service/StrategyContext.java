package com.juemuel.trend.service;

import com.juemuel.trend.strategy.TradingStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StrategyContext {

    private final Map<String, TradingStrategy> strategies;

    public StrategyContext(Map<String, TradingStrategy> strategies) {
        this.strategies = strategies;
    }

    public TradingStrategy getStrategy(String name) {
        return strategies.get(name);
    }
}
