package com.juemuel.trend.controller;

import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.context.IndicatorContext;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.service.BackTestService;
import com.juemuel.trend.context.StrategyContext;
import com.juemuel.trend.calculator.strategy.TradingStrategy;
import com.juemuel.trend.calculator.indicator.IndicatorCalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);
    @Autowired BackTestService backTestService;

    @Autowired
    private final StrategyContext strategyContext;

    @Autowired
    IndicatorContext indicatorContext;

    @Autowired
    IndexDataClient indexDataClient;
    public TestController(StrategyContext strategyContext) {
        this.strategyContext = strategyContext;
    }

    @GetMapping("/test/{market}/{code}")
    public List<IndexData> test(@PathVariable String market, @PathVariable String code) {
        Result<List<IndexData>> response = indexDataClient.getIndexData(market, code);  // 修改这里
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            log.warn("从数据源获取数据为空: code={}", market+code);
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
     * 查看已注入的策略
     * @return
     */
    @GetMapping("/test-strategies")
    public List<String> listStrategies() {
        return strategyContext.getAll().stream()
                .map(TradingStrategy::getName)
                .collect(Collectors.toList());
    }

    /**
     * 查看已注入的指标计算器
     * @return
     */
    @GetMapping("/test-indicators")
    public List<String> listIndicators() {
        return indicatorContext.getAll().stream()
                .map(IndicatorCalculator::getName)
                .collect(Collectors.toList());
    }

}

