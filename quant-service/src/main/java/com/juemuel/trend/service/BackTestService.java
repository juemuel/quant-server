package com.juemuel.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.strategy.TradingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class BackTestService {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);
    @Autowired
    IndexDataClient indexDataClient;
    @Autowired
    private IndicatorContext indicatorContext;
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

    public Map<String, Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge,
                                        List<IndexData> indexDatas, TradingStrategy strategy) {

        // Step1: 执行策略获取交易记录
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ma", ma);
        paramMap.put("buyRate", buyRate);
        paramMap.put("sellRate", sellRate);
        paramMap.put("serviceCharge", serviceCharge);

        List<Trade> trades = strategy.execute(indexDatas, paramMap);

        // Step2: 使用 IndicatorCalculator 计算指标
        Map<String, Object> result = new HashMap<>();

        result.put("annual_return",
                indicatorContext.get("annual_return").calculate(indexDatas, trades, paramMap));

        result.put("trade_stats",
                indicatorContext.get("trade_stats").calculate(indexDatas, trades, paramMap));

        result.put("trades", trades); // 也可返回原始交易记录
        return result;
    }
    private List<List<Float>> calculateMultipleMAs(List<IndexData> indexDatas, List<Integer> maPeriods) {
        List<List<Float>> maLists = new ArrayList<>();
        for (int period : maPeriods) {
            List<Float> maList = new ArrayList<>();
            for (int i = 0; i < indexDatas.size(); i++) {
                float ma = getMA(i, period, indexDatas);
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
     * 获取一年的指数投资收益
     * @param year
     * @param indexDatas
     * @return
     */
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;
//        log.info("getIndexIncome: {}", indexDatas);
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
            if (strDate == null || "0000-00-00".equals(strDate)) {
                log.info("无效日期：" + strDate);
                continue;  // 跳过无效日期
            }
//			Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }
    /**
     * 获取一年的趋势投资收益
     * @param year
     * @param profits
     * @return
     */
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
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

    /**
     * 计算完整时间范围内，每年的指数投资年收益和趋势投资年收益
     * @param indexDatas
     * @param profits
     * @return
     */
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        if (indexDatas == null || indexDatas.isEmpty() || profits == null || profits.isEmpty()) {
            log.info("计算年度收益时数据为空 {}", indexDatas);
            return result;
        }
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
//            System.out.println(year + ":"+indexDatas);
            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
        }
        return result;
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