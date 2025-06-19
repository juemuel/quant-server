package com.juemuel.trend.context;

import com.juemuel.trend.calculator.strategy.TradingStrategy;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * 策略的注册中心/上下文容器
 */
@Service
public class StrategyContext {

    private final Map<String, TradingStrategy> strategies;
    public StrategyContext(Map<String, TradingStrategy> strategies) {
        this.strategies = strategies;
        System.out.println("Loaded strategies: " + strategies.keySet());
    }

    public TradingStrategy getStrategy(String name) {
        return strategies.get(name);
    }

    public Collection<TradingStrategy> getAll() {
        return strategies.values();
    }
}
