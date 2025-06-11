package com.juemuel.trend.strategy;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RSIStrategy implements TradingStrategy {

    @Override
    public String getName() {
        return "rsi_strategy";
    }

    @Override
    public List<Trade> execute(List<IndexData> indexDatas, Map<String, Object> params) {
        int rsiPeriod = (Integer) params.get("rsi_period");
        float overbought = (Float) params.get("overbought");
        float oversold = (Float) params.get("oversold");
        float serviceCharge = (Float) params.get("serviceCharge");

        // 实现 RSI 相关交易逻辑
        // ...
        return null;
    }
}