package com.juemuel.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.service.BackTestService;
import com.juemuel.trend.context.StrategyContext;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
public class BackTestController {
    private static final Logger log = LoggerFactory.getLogger(BackTestController.class);
    @Autowired BackTestService backTestService;

    @Autowired
    private final StrategyContext strategyContext;


    public BackTestController(StrategyContext strategyContext) {
        this.strategyContext = strategyContext;
    }

    //Tips: 如果是url后直接加请求，那么不需要/
    @GetMapping("/simulate")
    public Result<Map<String, Object>> backTest(@RequestParam Map<String, String> allParams) throws Exception {

        // Step1: 解析策略参数
        log.info("前端入参 allParams: {}", allParams);
        StrategyParams strategyParams = StrategyParams.fromMap(allParams);
        String strategyName = strategyParams.getStrategyName();
        if (strategyName == null || strategyName.isEmpty()) {
            log.warn("策略名称不能为空");
            return Result.error(404, "策略名称不能为空");
        }
        // Step2: 获取指数数据
        List<IndexData> indexDatas = backTestService.listIndexData(strategyParams.getMarket(), strategyParams.getCode());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.info("没有获得指数回测数据");
            return Result.error(404, "未找到该指数的数据，请检查指数代码是否正确");
        }
        indexDatas = filterByDateRange(indexDatas, strategyParams.getStrStartDate(), strategyParams.getStrEndDate());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.warn("根据日期范围过滤后，没有可用数据");
            return Result.error(404, "指定日期范围内无数据，请调整时间范围");
        }

        // Step3: 执行策略
        return backTestService.simulate(strategyParams, indexDatas);

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

}