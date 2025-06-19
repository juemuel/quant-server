package com.juemuel.trend.calculator.strategy;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.juemuel.trend.calculator.position.PositionManager;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.context.SignalContext;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.util.IndexDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//Tips: Bean名称规则
// 1. 默认类名作为Bean名称MAStrategy
// 2. 使用@Component('指定Bean名称'）
@Component("ma_strategy")
public class MAStrategy implements TradingStrategy {



    @Override
    public String getName() {
        return "ma_strategy";
    }

    /**
     * 策略执行函数
     * 实际上时一个趋势投资的策略
     * 1. 买入逻辑：突破策略，高于某均线（阈值）进场；
     * 2. 卖出逻辑：趋势反转策略, 跌破高点（阈值）出场；
     * TODO：加入止损机制（例如最大回撤止损）、加入仓位控制（例如每次只买 1/2 仓位）、加入复合指标判断（如 MACD、RSI）
     * TODO：插拔动态阈值、指标策略（MA）；决策方式（简单突破、或形成、阈值）、策略类型
     * @param indexDatas
     * @param params+新的信号、风险、仓位模块
     * @return
     */
    @Override
    public List<Trade> execute(
            List<IndexData> indexDatas,
            StrategyParams params,
            SignalCondition signalCondition,
            RiskRule riskRule,
            PositionManager positionManager) {

        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        List<Trade> trades = new ArrayList<>();


        for (int i = 0; i < indexDatas.size(); i++) {
            IndexData current = indexDatas.get(i);

            // 卖出逻辑
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

            // 买入逻辑
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

    //TODO: 把此处的MA用MA指标代替
    /**
     * 计算MA
     * @param currentIndex
     * @param ma
     * @param indexDatas
     * @return
     */
    private float getMA(int currentIndex, int ma, List<IndexData> indexDatas) {
        if (currentIndex < ma - 1) {
            return 0;
        }
        float sum = 0;
        for (int i = currentIndex - ma + 1; i <= currentIndex; i++) {
            sum += indexDatas.get(i).getClosePoint();
        }
        return sum / ma;
    }
}
