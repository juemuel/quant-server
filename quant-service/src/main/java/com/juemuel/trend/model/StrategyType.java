package com.juemuel.trend.model;

public enum StrategyType {
    MA("ma_strategy"),
    BOLLINGER_BAND("bollinger_band_strategy"),
    MULTI_FACTOR("multi_factor_strategy");

    private final String value;

    StrategyType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
