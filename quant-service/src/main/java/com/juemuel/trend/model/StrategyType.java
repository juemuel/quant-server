package com.juemuel.trend.model;

public enum StrategyType {
    MA("ma_strategy"),
    BOLLINGER_BAND("bollinger_band_strategy"),
    CUSTOM("custom_strategy"); // 自定义策略

    private final String value;

    StrategyType(String value) {
        this.value = value;
    }

    public boolean isCustom() {
        return this == CUSTOM;
    }
}
