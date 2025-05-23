package com.juemuel.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.service.BackTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BackTestController {
    private static final Logger log = LoggerFactory.getLogger(BackTestController.class);
    @Autowired BackTestService backTestService;

    // @CrossOrigin和@CrossOrigin(origins = "http://localhost:8080")某个特定域名只能添加一个
    // @CroosOrigin默认设置了Access-Control-Allow-Origin: *
    // 但是浏览器不允许同时设置多处设置，这边在网关处设置了*，所以不再添加@CrossOrigin
    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
//    @CrossOrigin
    public Map<String,Object> backTest(
            @PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold
            ,@PathVariable("sellThreshold") float sellThreshold
            ,@PathVariable("serviceCharge") float serviceCharge
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate
    ) throws Exception {
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        if (allIndexDatas == null || allIndexDatas.isEmpty()) {
            log.info("没有获得指数回测数据");
            return new HashMap<>();  // 返回空结果
        }
        String indexStartDate = allIndexDatas.get(0).getDate();
        String indexEndDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        allIndexDatas = filterByDateRange(allIndexDatas,strStartDate, strEndDate);
        float sellRate = 0.95f;
        float buyRate = 1.05f;
        Map<String,?> simulateResult= backTestService.simulate(ma,sellRate, buyRate,serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");
        List<MaData> maDatas = (List<MaData>) simulateResult.get("allMaDatas");
        float years = backTestService.getYear(allIndexDatas);
        float indexIncomeTotal = (allIndexDatas.get(allIndexDatas.size()-1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1+indexIncomeTotal, 1/years) - 1;
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;

        int winCount = (Integer) simulateResult.get("winCount");
        int lossCount = (Integer) simulateResult.get("lossCount");
        float avgWinRate = (Float) simulateResult.get("avgWinRate");
        float avgLossRate = (Float) simulateResult.get("avgLossRate");

        List<AnnualProfit> annualProfits = (List<AnnualProfit>) simulateResult.get("annualProfits");

        Map<String,Object> result = new HashMap<>();
        // 指数、均线、本身收益
        result.put("indexDatas", allIndexDatas);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        result.put("maDatas", maDatas);
        // 收益数据
        result.put("annualProfits", annualProfits);
        result.put("years", years);
        List<Map<String, Object>> incomeStastics = Arrays.asList(
            createIncomeStasticsItem(years, "指数收益", indexIncomeTotal, indexIncomeAnnual),
            createIncomeStasticsItem(years,"趋势收益", trendIncomeTotal, trendIncomeAnnual)
        );
        result.put("incomeStastics", incomeStastics);
        // 交易数据
        result.put("trades", trades);
        List<Map<String, Object>> tradeStastics = Arrays.asList(
            createTradeStasticsItem("趋势交易", winCount, lossCount, avgWinRate, avgLossRate)
        );
        result.put("tradeStastics", tradeStastics);
        return result;
    }

    /**
     * 根据日期范围过滤
     * @param allIndexDatas
     * @param strStartDate
     * @param strEndDate
     * @return
     */
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) )
            return allIndexDatas;
        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);

        for (IndexData indexData : allIndexDatas) {
            Date date =DateUtil.parse(indexData.getDate());
            if(
                    date.getTime()>=startDate.getTime() &&
                            date.getTime()<=endDate.getTime()
            ) {
                result.add(indexData);
            }
        }
        return result;
    }

    /**
     * 创建收益小结数组对象
     * @param years
     * @param title
     * @param total
     * @param annual
     * @return
     */
    private static Map<String, Object> createIncomeStasticsItem(Float years, String title, Float total, Float annual) {
        Map<String, Object> data = new HashMap<>();
        data.put("years", years);
        data.put("title", title);
        data.put("incomeTotal", total);
        data.put("incomeAnnual", annual);
        return data;
    }

    /**
     * 创建交易小结数组对象
     * @param title
     * @param winCount
     * @param lossCount
     * @param avgWinRate
     * @param avgLossRate
     * @return
     */
    private static Map<String, Object> createTradeStasticsItem(String title, Integer winCount, Integer lossCount, Float avgWinRate, Float avgLossRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("winCount", winCount);
        data.put("lossCount", lossCount);
        data.put("avgWinRate", avgWinRate);
        data.put("avgLossRate", avgLossRate);
        return data;
    }
}