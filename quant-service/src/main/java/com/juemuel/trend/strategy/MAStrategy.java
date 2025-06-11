package com.juemuel.trend.strategy;

import cn.hutool.core.date.DateUtil;
import com.juemuel.trend.pojo.*;
import org.springframework.stereotype.Component;

import java.util.*;

//Tips: Bean名称规则
// 1. 默认类名作为Bean名称MAStrategy
// 2. 使用@Component('指定Bean名称'）
@Component("ma_strategy")
public class MAStrategy implements TradingStrategy  {
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
     *
     * @param indexDatas
     * @param params
     * @return
     */
    @Override
    public List<Trade> execute(List<IndexData> indexDatas, Map<String, Object> params) {
        List<Trade> trades = new ArrayList<>();
        int ma = (Integer) params.get("ma");
        float buyRate = (Float) params.get("buyRate");
        float sellRate = (Float) params.get("sellRate");
        float serviceCharge = (Float) params.get("serviceCharge");
        // 初始现金
        float initCash = 1000;
        float cash = initCash;
        float share = 0; // 持有的股票数量
        float value = 0;  // 当前持有资产的价值
        // 初始化交易统计变量
        float totalWinRate = 0;  // 总赢利率
        float totalLossRate = 0;  // 总亏损率

        // 遍历所有数据
        float init =0; // 初始化收盘价
//        log.info("初始的indexDatas{}", indexDatas);
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();
        for (int i = 0; i<indexDatas.size() ; i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i, ma, indexDatas);  // 根据传入的ma，计算算法参考的移动平均线
            float max = getMax(i, ma, indexDatas);  // 根据传入的ma，计算算法参考的最大值
            float increase_rate = closePoint / avg;  // 上涨比例
            float decrease_rate = closePoint / max;  // 下跌比例
            // 操作逻辑
            if(avg!=0) {
                // 买入：收盘价超过买入阈值
                if(increase_rate>buyRate  ) {
                    if (share == 0) {  // 如果没有持股
                        share = cash / closePoint;  // 用现金购买股票
                        cash = 0;  // 现金清零

                        // 创建交易记录
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                // 卖出：收盘价低于卖出阈值
                else if(decrease_rate<sellRate ) {
                    if (share != 0) {  // 如果持有股票
                        cash = closePoint * share * (1 - serviceCharge);  // 出售股票并扣除手续费
                        share = 0;  // 清空持有的股票

                        // 更新最后一个交易记录
                        Trade trade = trades.get(trades.size() - 1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        float rate = cash / initCash;  // 计算收益率
                        trade.setRate(rate);

                        // 计算盈亏情况
                        if (trade.getSellClosePoint() - trade.getBuyClosePoint() > 0) {
                            totalWinRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                        } else {
                            totalLossRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                        }
                    }
                }
                // 不操作
                else{

                }
            }
            // 计算当前持有的资产价值
            if(share!=0) {
                value = closePoint * share;
            }
            else {
                value = cash;
            }
        }

        // 构建结果映射
//        map.put("allMaDatas", allMaDatas);
        return trades;
    }
    /**
     * 计算日期范围内，指数的最大值
     * @param currentIndex
     * @param ma
     * @param indexDatas
     * @return
     */
    private float getMax(int currentIndex, int ma, List<IndexData> indexDatas) {
        if (currentIndex < ma - 1) {
            return 0;
        }
        float max = Float.MIN_VALUE;
        for (int i = currentIndex - ma + 1; i <= currentIndex; i++) {
            max = Math.max(max, indexDatas.get(i).getClosePoint());
        }
        return max;
    }
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
