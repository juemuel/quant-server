package com.juemuel.trend.calculator.risk;

import com.juemuel.trend.pojo.Trade;
import com.juemuel.trend.pojo.IndexData;

/**
 * 风险控制规则接口
 */
public interface RiskRule {
    boolean shouldExit(Trade trade, IndexData currentData);
}
