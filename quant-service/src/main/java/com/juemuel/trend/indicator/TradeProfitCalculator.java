package com.juemuel.trend.indicator;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.pojo.AnnualProfit;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Profit;
import com.juemuel.trend.pojo.Trade;
import com.juemuel.trend.service.BackTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 策略交易收益计算器
 * totalRate：整体复利收益
 * annualRate: 年化收益率
 * strategyDailyProfits
 * strategyAnnualProfits
 */
@Component("trade_profit")
public class TradeProfitCalculator implements IndicatorCalculator {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);
    @Autowired
    private BackTestService backTestService;
    @Override
    public String getName() {
        return "trade_profit";
    }

    @Override
    public Object calculate(List<IndexData> indexDatas, List<Trade> trades, Map<String, Object> params) {
        float initCash = 1000;
        float finalValue = trades.isEmpty() ? initCash : trades.get(trades.size()-1).getRate() * initCash;
        float years = getYears(indexDatas);
        float rate = (finalValue - initCash) / initCash; // 总收益率
        float annualRate = years > 0 ? (float)Math.pow(1 + rate, 1/years) - 1 : 0; // 年化收益率

        List<Profit> strategyDailyProfits = caculateDailyProfits(trades, indexDatas); // 策略日收益
        List<AnnualProfit> strategyAnnualProfits = caculateAnnualProfits(indexDatas, trades); // 策略年收益

        Map<String, Object> result = new HashMap<>();
        result.put("totalRate", rate);
        result.put("annualRate", annualRate);;
        result.put("strategyDailyProfits", strategyDailyProfits);
        result.put("strategyAnnualProfits", strategyAnnualProfits);
        return result;
    }

    /**
     * 计算策略交易下的每日资产收益和收益率
     * @param trades
     * @return
     */
    private List<Profit> caculateDailyProfits(List<Trade> trades, List<IndexData> indexDatas) {
        Map<String, Trade> sellDateToTrade = new HashMap<>();
        for (Trade trade : trades) {
            sellDateToTrade.put(trade.getSellDate(), trade);
        }

        List<Profit> profits = new ArrayList<>();
        float initValue = 1000;   // 初始资金
        float currentValue = initValue;

        for (IndexData data : indexDatas) {
            String date = data.getDate();
            if ("0000-00-00".equals(date)) continue;

            Trade trade = sellDateToTrade.get(date);
            if (trade != null) {
                float newValue = trade.getRate() * initValue; // 使用初始资金计算最终价值
                float dailyRate = (newValue - currentValue) / currentValue;
                currentValue = newValue;

                Profit profit = new Profit();
                profit.setDate(date);
                profit.setValue(newValue);
                profit.setRate(dailyRate);
                profit.setCumulativeRate((newValue - initValue) / initValue); // 累计收益率

                profits.add(profit);
            } else {
                // 没有交易发生，保持上一日资产值
                Profit profit = new Profit();
                profit.setDate(date);
                profit.setValue(currentValue);
                profit.setRate(0f); // 无交易，当日无变化
                profit.setCumulativeRate((currentValue - initValue) / initValue); // 累计收益率

                profits.add(profit);
            }
        }

        return profits;
    }

    /**
     * 计算策略交易下的年资产收益和收益率
     * @param indexDatas
     * @param trades
     * @return
     */
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Trade> trades) {
        // Step1: 获取完整的每日资产曲线（包含所有日期）
        List<Profit> profits = caculateDailyProfits(trades, indexDatas);

        List<AnnualProfit> result = new ArrayList<>();
        if (indexDatas.isEmpty() || profits.isEmpty()) return result;

        Map<Integer, Float> yearlyValues = new HashMap<>();

        // Step2: 遍历每日资产曲线，记录每年最后一天的资产值
        for (Profit profit : profits) {
            String dateStr = profit.getDate();
            if ("0000-00-00".equals(dateStr)) continue;

            int year = extractYear(dateStr);
            yearlyValues.put(year, profit.getValue()); // 覆盖写入，最终保留的是该年的最后一条记录
        }

        // Step3: 构建年收益（复利计算）
        Integer[] years = yearlyValues.keySet().stream().sorted().toArray(Integer[]::new);

        float initValue = 1000;
        float prevValue = initValue;
        for (int i = 0; i < years.length; i++) {
            int year = years[i];
            float value = yearlyValues.get(year);
            float yoyRate = (value - prevValue) / prevValue; // 年复利收益
            float cumRate = (value - initValue) / initValue;   // 累计收益

            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
            annualProfit.setValue(value);
            annualProfit.setRate(yoyRate);
            annualProfit.setCumulativeRate(cumRate); // 累计收益

            result.add(annualProfit);
            prevValue = value;
        }

        return result;
    }

    /**
     * 从日期字符串中提取年份
     * @param dateStr
     * @return
     */
    private int extractYear(String dateStr) {
        return Integer.parseInt(dateStr.split("-")[0]);
    }
    /**
     * 计算当前的时间范围有多少年（计算起始年份和结束年份）
     * @param indexDatas
     * @return
     */
    public float getYears(List<IndexData> indexDatas) {
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.warn("无法计算年份：数据为空");
            return 0; // 返回默认值避免异常
        }
        try {
            String sDateStart = indexDatas.get(0).getDate();
            String sDateEnd = indexDatas.get(indexDatas.size() - 1).getDate();

            if (StrUtil.isBlankOrUndefined(sDateStart) || StrUtil.isBlankOrUndefined(sDateEnd)) {
                log.warn("日期字段为空，无法计算年份");
                return 0;
            }

            Date dateStart = DateUtil.parse(sDateStart);
            Date dateEnd = DateUtil.parse(sDateEnd);

            long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
            return days / 365f;
        } catch (Exception e) {
            log.error("计算年份时发生错误", e);
            return 0;
        }
    }

}
