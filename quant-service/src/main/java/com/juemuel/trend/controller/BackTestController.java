package com.juemuel.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.calculator.position.PositionManager;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.context.SignalContext;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.*;
import com.juemuel.trend.service.BackTestService;
import com.juemuel.trend.context.IndicatorContext;
import com.juemuel.trend.context.StrategyContext;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.juemuel.trend.context.RiskContext;
import com.juemuel.trend.context.PositionContext;
import com.juemuel.trend.context.TradeContext;

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

    @GetMapping("/simulate/{strategy}")
    public Result<Map<String, Object>> backTest(@PathVariable("strategy") String strategyName,
                                                @RequestParam Map<String, String> allParams) throws Exception {

        // Step1: 解析策略参数
        StrategyParams params = StrategyParams.fromMap(allParams);

        // Step2: 获取指数数据
        List<IndexData> indexDatas = backTestService.listIndexData(params.getMarket(), params.getCode());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.info("没有获得指数回测数据");
            return Result.error(404, "未找到该指数的数据，请检查指数代码是否正确");
        }

        // Step3: 根据日期范围过滤数据
        indexDatas = filterByDateRange(indexDatas, params.getStrStartDate(), params.getStrEndDate());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.warn("根据日期范围过滤后，没有可用数据");
            return Result.error(404, "指定日期范围内无数据，请调整时间范围");
        }

        // Step4: 获取策略对象
        TradingStrategy strategy = strategyContext.getStrategy(strategyName);
        if (strategy == null) {
            log.warn("找不到策略：{}", strategyName);
            return Result.error(404, "不支持的策略：" + strategyName);
        } else {
            params.setStrategyName(strategyName);
        }

        // Step5: 调用 Service 执行完整回测
        return backTestService.simulate(params, indexDatas, strategy);
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