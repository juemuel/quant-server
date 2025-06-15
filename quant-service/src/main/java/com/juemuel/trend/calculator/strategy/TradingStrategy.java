package com.juemuel.trend.calculator.strategy;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.position.PositionManager;

import java.util.List;
import java.util.Map;

public interface TradingStrategy {
    String getName();

    List<Trade> execute(
            List<IndexData> indexDatas,
            Map<String, Object> params,
            SignalCondition signalCondition,
            RiskRule riskRule,
            PositionManager positionManager);
}
