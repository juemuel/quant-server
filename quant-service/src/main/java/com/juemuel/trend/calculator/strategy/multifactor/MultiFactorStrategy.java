package com.juemuel.trend.calculator.strategy.multifactor;

import com.juemuel.trend.calculator.position.PositionManager;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import com.juemuel.trend.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("multi_factor_strategy")
@Slf4j
public class MultiFactorStrategy implements TradingStrategy {

    @Override
    public String getName() {
        return "multi_factor_strategy";
    }

    @Override
    public List<Trade> execute(
            List<IndexData> indexDatas,
            StrategyParams params,
            SignalCondition signalCondition,
            RiskRule riskRule,
            PositionManager positionManager,
            List<IndicatorData> indicatorDatas,
            Map<String, List<Float>> factorValues) {


        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        List<Trade> trades = new ArrayList<>();
        List<Float> maFactors = factorValues.getOrDefault("ma_factor", Collections.emptyList());
        List<Float> rsiFactors = factorValues.getOrDefault("rsi_factor", Collections.emptyList());


        for (int i = 0; i < indexDatas.size(); i++) {
            IndexData current = indexDatas.get(i);

            // 卖出逻辑，风险管理，卖出信号判定
            if (share > 0 &&
                    (params.isEnableRiskManagement() && riskRule.shouldExit(null, current) ||
                            signalCondition.isSellSignal(indexDatas, i, params.getSignalParams()))) {

                cash = current.getClosePoint() * share * (1 - params.getServiceCharge());
                share = 0;

                Trade trade = trades.get(trades.size() - 1);
                trade.setSellDate(current.getDate());
                trade.setSellClosePoint(current.getClosePoint());
                continue;
            }

            // 多因子买入逻辑
            if (share == 0 && i < maFactors.size() && i < rsiFactors.size()) {
                float maFactor = maFactors.get(i);
                float rsiFactor = rsiFactors.get(i);
                // factor是输入；signal是基于factor的决策逻辑；
                if (maFactor > 0 && rsiFactor < 30) {
                    share = positionManager.calculatePosition(cash, current);
                    cash -= share * current.getClosePoint();

                    Trade trade = new Trade();
                    trade.setBuyDate(current.getDate());
                    trade.setBuyClosePoint(current.getClosePoint());
                    trades.add(trade);
                }
            }
        }

        return trades;
    }
}
