package com.juemuel.trend.context;

import com.juemuel.trend.calculator.risk.RiskRule;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class RiskContext {
    private final Map<String, RiskRule> riskRules;

    public RiskContext(Map<String, RiskRule> riskRules) {
        this.riskRules = riskRules;
    }

    public RiskRule get(String name) {
        return riskRules.get(name);
    }

    public Collection<RiskRule> getAll() {
        return riskRules.values();
    }
}
