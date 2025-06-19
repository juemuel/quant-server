package com.juemuel.trend.pojo;

import cn.hutool.core.convert.Convert;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Data
public class StrategyParams {
    // 基本信息
    private String strategyName;
    private String strategyType;
    private String market;
    private String code;
    // 保留类型标识字段
    private String signalType; // signalType: "trend_reversal_signal", "multi_factor_signal"
    private String riskRuleType; // riskRuleType: "stop_loss_rule", "take_profit_rule"
    private String positionType; // positionType: "fixed_position", "dynamic_position"
    // 仓位管理
    private boolean enablePositionManagement = false;
    private float maxPositionsNum = 20.0f; // TODO:

    // 风险控制
    private boolean enableRiskManagement = false;
    private float stopLoss = 0.08f;  // TODO: 8%
    private float takeProfit = 0.15f; // TODO: 15%

    // 交易执行
    private float buyThreshold = 1.02f;
    private float sellThreshold = 0.98f;
    private float serviceCharge = 0.0003f;
    private int rebalancePeriod = 20; // TODO:
    private float stampDutyRate = 0.001f; // TODO:
    private String slippageModel = "fixed"; // TODO:
    private float fixedSlippage = 0.001f; //

    // TODO：策略信号，其中的策略信号会根据前端选择的测类（单因子策略、多因子策略以及自定义策略）来动态加载相关的参数，我该怎么处理控制呢？
    private String strStartDate;
    private String strEndDate;
    private int ma;
    private List<Integer> maPeriods; // TODO“多周期 MA
    private Map<String, Object> signalParams = new HashMap<>(); // TODO: 策略信号，目前缺少signalType考虑是否固定后端判断策略类型来填写
    private Map<String, Object> extraParams = new HashMap<>();



    public StrategyParams() {}

    public StrategyParams(String strategyName, String market, String code, int ma, float buyThreshold, float sellThreshold,
                          float serviceCharge, String strStartDate, String strEndDate) {
        this.strategyName = strategyName;
        this.code = code;
        this.market = market;
        this.ma = ma;
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.serviceCharge = serviceCharge;
        this.strStartDate = strStartDate;
        this.strEndDate = strEndDate;
    }

    private static final Logger log = LoggerFactory.getLogger(StrategyParams.class);

    // 从 Map 构建 StrategyParams（适用于 @RequestParam 或 URL 参数）
    public static StrategyParams fromMap(Map<String, String> rawParams) {
        log.info("[map->params]：{}", rawParams != null ? rawParams.get("strategyName") : "null");

        StrategyParams params = new StrategyParams();

        if (rawParams == null) {
            log.warn("rawParams is null");
            return params;
        }

        params.setStrategyName(rawParams.get("strategyName"));
        params.setStrategyType(rawParams.get("strategyType"));
        params.setCode(rawParams.get("code"));
        params.setMarket(rawParams.get("market"));

        // 仓位管理
        params.setEnablePositionManagement(Boolean.parseBoolean(rawParams.get("enablePositionManagement")));
        params.setPositionType(rawParams.getOrDefault("positionType", "fixed_position"));
        params.setMaxPositionsNum(Convert.toFloat(rawParams.get("maxPositionsNum"), 20.0f));
        // 风控规则
        params.setEnableRiskManagement(Boolean.parseBoolean(rawParams.get("enableRiskManagement")));
        params.setRiskRuleType(rawParams.getOrDefault("riskRuleType", "stop_loss_rule"));
        params.setStopLoss(Convert.toFloat(rawParams.get("stopLoss"), 0.08f));
        params.setTakeProfit(Convert.toFloat(rawParams.get("takeProfit"), 0.15f));

        // 交易执行
        params.setBuyThreshold(Convert.toFloat(rawParams.get("buyThreshold"), 1.02f));
        params.setSellThreshold(Convert.toFloat(rawParams.get("sellThreshold"), 0.98f));
        params.setServiceCharge(Convert.toFloat(rawParams.get("serviceCharge"), 0.0003f));
        params.setRebalancePeriod(Convert.toInt(rawParams.get("rebalancePeriod"), 20));
        params.setStampDutyRate(Convert.toFloat(rawParams.get("stampDutyRate"), 0.001f));
        params.setSlippageModel(rawParams.getOrDefault("slippageModel", "fixed"));
        params.setFixedSlippage(Convert.toFloat(rawParams.get("fixedSlippage"), 0.001f));
        // 策略信号
        params.setSignalType(rawParams.getOrDefault("signalType", "trend_reversal_signal"));
        params.setMa(Convert.toInt(rawParams.get("ma"), 20));
        params.setStrStartDate(rawParams.get("startDate"));
        params.setStrEndDate(rawParams.get("endDate"));

        // 其他未匹配参数放入 signalParams
        Set<String> excludedKeys = new HashSet<>(Arrays.asList(
                "strategyName", "code", "market",
                "enablePositionManagement", "positionType", "maxPositionsNum",
                "enableRiskManagement", "riskRuleType", "stopLoss", "takeProfit",
                "signalType", "buyThreshold", "sellThreshold", "rebalancePeriod",
                "serviceCharge", "stampDutyRate", "slippageModel", "fixedSlippage",
                "startDate", "endDate"
        ));
        for (Map.Entry<String, String> entry : rawParams.entrySet()) {
            if (!excludedKeys.contains(entry.getKey())) {
                try {
                    params.getSignalParams().put(entry.getKey(), Float.parseFloat(entry.getValue()));
                } catch (NumberFormatException e) {
                    params.getSignalParams().put(entry.getKey(), entry.getValue()); // 保留原字符串
                }
            }
        }



        return params;
    }
}
