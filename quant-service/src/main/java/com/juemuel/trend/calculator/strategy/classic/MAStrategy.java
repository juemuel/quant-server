package com.juemuel.trend.calculator.strategy.classic;

import com.juemuel.trend.calculator.position.PositionManager;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.service.IndicatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("ma_strategy")
@Slf4j
public class MAStrategy implements TradingStrategy {

    @Autowired
    private IndicatorService indicatorService;

    @Override
    public String getName() {
        return "ma_strategy";
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

        for (int i = 0; i < indexDatas.size(); i++) {
            IndexData current = indexDatas.get(i);

            // 卖出逻辑，持有股票，风险管理触发，卖出信号判定
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

            // 买入逻辑，未持有股票，买入信号判定
            if (share == 0 &&
                    (signalCondition.isBuySignal(indexDatas, i, params.getSignalParams()))) {

                share = positionManager.calculatePosition(cash, current);
                cash -= share * current.getClosePoint();

                Trade trade = new Trade();
                trade.setBuyDate(current.getDate());
                trade.setBuyClosePoint(current.getClosePoint());
                trades.add(trade);
            }
        }

        return trades;
    }
}
