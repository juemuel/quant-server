package com.juemuel.trend.context;

import com.juemuel.trend.calculator.trade.TradeCalculator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * 交易统计的上下文
 */
@Service
public class TradeContext {
    private final Map<String, TradeCalculator> tradeCalculators;

    public TradeContext(Map<String, TradeCalculator> tradeCalculators) {
        this.tradeCalculators = tradeCalculators;
        System.out.println("Loaded tradeCalculators: " + tradeCalculators.keySet());
    }

    public TradeCalculator get(String name) {
        return tradeCalculators.get(name);
    }

    public Collection<TradeCalculator> getAll() {
        return tradeCalculators.values();
    }
}
