package com.juemuel.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.context.IndicatorContext;
import com.juemuel.trend.context.TradeContext;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import com.juemuel.trend.util.DateUtilEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.juemuel.trend.util.IndexDataUtil;


@Service
public class BackTestService {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);
    @Autowired
    IndexDataClient indexDataClient;
    @Autowired
    private IndicatorContext indicatorContext;
    @Autowired
    private TradeContext tradeContext;
    public List<IndexData> listIndexData(String code) {
        Result<List<IndexData>> response = indexDataClient.getIndexData(code);  // 修改这里
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            log.warn("从数据源获取数据为空: code={}", code);
            return new ArrayList<>();
        }
        List<IndexData> result = response.getData()
                .stream()
                .filter(data -> data != null && data.getDate() != null && !"0000-00-00".equals(data.getDate()))
                .collect(Collectors.toList());
        log.info("有效数据: size={}", result.size());
        Collections.reverse(result);
        return result;
    }

    /**
     * TODO: 回测函数考虑异步
     * @param params
     * @param indexDatas
     * @param strategy
     * @return
     */
    public Map<String, Object> simulate(StrategyParams rawParams,
                                        List<IndexData> indexDatas,
                                        TradingStrategy strategy) {


        // Step1: 执行策略（构建策略参数、策略信息，并执行策略获取交易记录）
        Map<String, Object> strategyParams = buildStrategyParamsMap(rawParams);
        List<Trade> trades = strategy.execute(indexDatas, strategyParams);
        // Step2: 计算指标，构建返回内容
        Map<String, Object> result = new HashMap<>();
        // 指数信息
        result.put("index_info", buildIndexInfo(indexDatas));
        // 策略信息
        result.put("strategy_info", buildStrategyInfo(strategy, rawParams));
        // 交易记录
        result.put("trades", trades);
        // 交易统计（通过交易计算器）
        result.put("trade_stats", tradeContext.get("trade_stats").calculate(indexDatas, trades, strategyParams));
        // 交易收益（通过交易计算器）
        result.put("trade_profit",
                tradeContext.get("trade_profit").calculate(indexDatas, trades, strategyParams));
        return result;
    }

    private Map<String, Object> buildStrategyInfo(TradingStrategy strategy, StrategyParams params) {
        Map<String, Object> strategyInfo = new HashMap<>();
        strategyInfo.put("strategyName", strategy.getName());
        strategyInfo.put("parameters", buildStrategyParamsMap(params));
        return strategyInfo;
    }
    private Map<String, Object> buildStrategyParamsMap(StrategyParams params) {
        Map<String, Object> strategyParams = new HashMap<>();
        strategyParams.put("serviceCharge", params.getServiceCharge());
        strategyParams.put("ma", params.getMa());
        strategyParams.put("buyRate", params.getBuyThreshold());
        strategyParams.put("sellRate", params.getSellThreshold());
        strategyParams.put("strategyStart", params.getStrStartDate());
        strategyParams.put("strategyEnd", params.getStrEndDate());
        return strategyParams;
    }
    private Map<String, Object> buildIndexInfo(List<IndexData> indexDatas) {
        Map<String, Object> indexInfo = new HashMap<>();
        if (!indexDatas.isEmpty()) {
            String startDate = indexDatas.get(0).getDate();
            String endDate = indexDatas.get(indexDatas.size() - 1).getDate();
            float years = DateUtilEx.getYearsFromIndexDatas(indexDatas);
            int totalDays = (int) (years * 365);
            indexInfo.put("filtered_index_start", startDate);
            indexInfo.put("filtered_index_end", endDate);
            indexInfo.put("indexDatas", indexDatas);
            indexInfo.put("total_days", totalDays);
            indexInfo.put("years", years);
        }
        return indexInfo;
    }
    private List<List<Float>> calculateMultipleMAs(List<IndexData> indexDatas, List<Integer> maPeriods) {
        List<List<Float>> maLists = new ArrayList<>();
        for (int period : maPeriods) {
            List<Float> maList = new ArrayList<>();
            for (int i = 0; i < indexDatas.size(); i++) {
                float ma = IndexDataUtil.getMA(i, period, indexDatas);
                maList.add(ma);
            }
            maLists.add(maList);
        }
        return maLists;
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



}