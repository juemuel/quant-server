package com.juemuel.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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

import java.util.*;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
public class BackTestController {
    private static final Logger log = LoggerFactory.getLogger(BackTestController.class);
    @Autowired BackTestService backTestService;

    @Autowired
    private final StrategyContext strategyContext;
    @Autowired
    private IndicatorContext indicatorContext;

    public BackTestController(StrategyContext strategyContext) {
        this.strategyContext = strategyContext;
    }

    @GetMapping("/simulate/{strategy}")
    public Result<Map<String,Object>> backTest(@PathVariable("strategy") String strategyName,
            @RequestParam Map<String, String> allParams
    ) throws Exception {
        // Step1: 解析参数
        StrategyParams params = StrategyParams.fromMap(allParams);

        // Step2 处理指数数据
        List<IndexData> indexDatas = backTestService.listIndexData(params.getCode());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.info("没有获得指数回测数据");
            return Result.error(404, "未找到该指数的数据，请检查指数代码是否正确");
        }
        // Step3: 按日期过滤
        String indexStartDate = indexDatas.get(0).getDate();
        String indexEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        indexDatas = filterByDateRange(indexDatas, params.getStrStartDate(), params.getStrEndDate());
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.warn("根据日期范围过滤后，没有可用数据");
            return Result.error(404, "指定日期范围内无数据，请调整时间范围");
        }
        // Step4: 获取并执行策略
        log.info("当前可用策略列表：{}", strategyContext.getAll().size());
        for (TradingStrategy s : strategyContext.getAll()) {
            log.info("策略 Bean 名称：{}, 名称：{}", s.getClass().getSimpleName(), s.getName());
        }
        TradingStrategy strategy = strategyContext.getStrategy(strategyName);
        if (strategy == null) {
            log.warn("找不到策略：{}", strategyName);
            return Result.error(404, "不支持的策略：" + strategyName);
        }
        // Step5: 调用 Service 执行完整回测
        Map<String, Object> result = backTestService.simulate(
                params.getMa(),
                params.getSellThreshold(),
                params.getBuyThreshold(),
                params.getServiceCharge(),
                indexDatas,
                strategy
        );
        return Result.success(result);
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