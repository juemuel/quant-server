package com.juemuel.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.context.IndicatorContext;
import com.juemuel.trend.context.TradeContext;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
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
     * @param ma
     * @param sellRate
     * @param buyRate
     * @param serviceCharge
     * @param indexDatas
     * @param strategy
     * @return
     */
    public Map<String, Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge,
                                        List<IndexData> indexDatas, TradingStrategy strategy) {

        // Step1: 执行策略获取交易记录
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("strategyName", strategy.getName());
        paramMap.put("ma", ma);
        paramMap.put("buyRate", buyRate);
        paramMap.put("sellRate", sellRate);
        paramMap.put("serviceCharge", serviceCharge);

        List<Trade> trades = strategy.execute(indexDatas, paramMap);

        // Step2: 使用 IndicatorCalculator 计算指标
        Map<String, Object> result = new HashMap<>();
        // 返回交易记录
        result.put("trades", trades);
        // 返回交易统计
        result.put("trade_stats",
                tradeContext.get("trade_stats").calculate(indexDatas, trades, paramMap));
        // 返回年化收益
        result.put("trade_profit",
                tradeContext.get("trade_profit").calculate(indexDatas, trades, paramMap));
        return result;
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



    /**
     * 获取某日期的年份
     * @param date
     * @return
     */
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }
}