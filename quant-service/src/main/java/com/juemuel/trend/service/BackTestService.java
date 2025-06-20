package com.juemuel.trend.service;

import com.juemuel.trend.calculator.position.PositionManager;
import com.juemuel.trend.calculator.risk.RiskRule;
import com.juemuel.trend.calculator.signal.SignalCondition;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.context.*;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.model.SlippageModel;
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
    @Autowired
    private StrategyContext strategyContext;
    @Autowired
    private SignalContext signalContext;
    @Autowired
    private RiskContext riskContext;
    @Autowired
    private PositionContext positionContext;
    @Autowired
    private IndicatorService indicatorService;
    @Autowired
    private FactorService factorService;
    public List<IndexData> listIndexData(String market, String code) {
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
     * 执行完整回测逻辑，返回封装好的 Result 对象
     */
    public Result<Map<String, Object>> simulate(StrategyParams strategyParams,
                                                List<IndexData> indexDatas) {
        log.info("[simulate] strategyName: {}", strategyParams.getStrategyName());

        try {
            // Step1: 获取各模块实例
            // 根据信号类型获得信号条件
            SignalCondition signalCondition = signalContext.get(strategyParams.getSignalType());
            RiskRule riskRule = riskContext.get(strategyParams.getRiskRuleType());
            PositionManager positionManager = positionContext.get(strategyParams.getPositionType());

            // Step2: 获取策略对象（原 Controller 中的逻辑）
            TradingStrategy strategy = strategyContext.getStrategy(strategyParams.getStrategyName());
            if (strategy == null) {
                return Result.error(404, "不支持的策略：" + strategyParams.getStrategyName());
            }
            // Step3: 预先计算指标和因子
            List<IndicatorData> indicatorDatas = indicatorService.calculateIndicators(indexDatas);
            Map<String, List<Float>> factorValues = factorService.extractFactors(indexDatas, indicatorDatas);


            // Step4: 执行策略
            List<Trade> trades = strategy.execute(
                    indexDatas,
                    strategyParams,
                    signalCondition,
                    riskRule,
                    positionManager,
                    indicatorDatas,
                    factorValues
            );
            // Step5: 构建响应数据（我想保证顺序就用了LinkedHashMap）
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("strategy_info", buildStrategyInfo(strategy, strategyParams));
            result.put("trade_stats", tradeContext.get("trade_stats").calculate(indexDatas, trades, buildStrategyParamsMap(strategyParams)));
            result.put("trade_profit", tradeContext.get("trade_profit").calculate(indexDatas, trades, buildStrategyParamsMap(strategyParams)));
            result.put("trades", trades);
            result.put("index_info", buildIndexInfo(indexDatas));
            // 可选：将因子和指标也返回用于前端展示
            result.put("indicators", indicatorDatas);
            result.put("factors", factorValues);
            return Result.success(result);
        } catch (Exception e) {
            log.error("回测执行异常", e);
            return Result.error(500, "回测执行异常：" + e.getMessage());
        }
    }

    private Map<String, Object> buildStrategyInfo(TradingStrategy strategy, StrategyParams params) {
        Map<String, Object> strategyInfo = new HashMap<>();
        strategyInfo.put("strategyName", strategy.getName());
        strategyInfo.put("parameters", buildStrategyParamsMap(params));
        return strategyInfo;
    }
    private Map<String, Object> buildStrategyParamsMap(StrategyParams params) {
        log.info("[params->map] params 内容: {}", params);
        Map<String, Object> strategyParams = new HashMap<>();
        strategyParams.put("strategyName", params.getStrategyName());
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
    //TODO:把计算多个MA值的逻辑迁移到MA计算器中
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
    private float applySlippage(float price, float slippage, SlippageModel model) {
        switch (model) {
            case FIXED:
                return price * (1 + slippage);
            case LINEAR:
                return price * (1 + slippage * 0.5f); // 示例线性计算
            case SQRT:
                return price * (1 + (float)Math.sqrt(slippage));
            default:
                return price;
        }
    }

}