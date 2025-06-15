package com.juemuel.trend.calculator.risk;

import com.juemuel.trend.pojo.Trade;
import com.juemuel.trend.pojo.IndexData;
import org.springframework.stereotype.Component;

/**
 * 固定百分比止损规则
 */
@Component("stop_loss_rule")
public class StopLossRule implements RiskRule {
    private float stopLossRate = 0.05f; // 默认止损 5%

    @Override
    public boolean shouldExit(Trade trade, IndexData currentData) {
        if (trade == null || trade.getBuyClosePoint() <= 0) return false;

        float price = currentData.getClosePoint();
        float lossRate = (trade.getBuyClosePoint() - price) / trade.getBuyClosePoint();
        return lossRate >= stopLossRate;
    }
}
