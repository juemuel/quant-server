package com.juemuel.trend.calculator.trade;

import com.juemuel.trend.controller.BackTestController;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易统计计算器
 */
@Component("trade_stats")
public class TradeStatisticsCalculator implements TradeCalculator {
    @Override
    public String getName() {
        return "trade_stats";
    }
    private static final Logger log = LoggerFactory.getLogger(TradeStatisticsCalculator.class);

    @Override
    public Object calculate(List<IndexData> data, List<Trade> trades, Map<String, Object> params) {
        log.info("[calculate] strategyName: {}", params.get("strategyName"));
        String strategyName = (String) params.get("strategyName");
        int winCount = 0;
        float totalWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;

        for (Trade trade : trades) {
            float profitRate = (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
            if (profitRate > 0) {
                winCount++;
                totalWinRate += profitRate;
            } else {
                lossCount++;
                totalLossRate += profitRate;
            }
        }

        float avgWinRate = winCount > 0 ? totalWinRate / winCount : 0;
        float avgLossRate = lossCount > 0 ? totalLossRate / lossCount : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("strategyName", strategyName);
        stats.put("winCount", winCount);
        stats.put("lossCount", lossCount);
        stats.put("avgWinRate", avgWinRate);
        stats.put("avgLossRate", avgLossRate);
        stats.put("totalTradeCount",  winCount + lossCount);
        stats.put("winRate",  winCount / (float) (winCount + lossCount));
        return stats;
    }
}
