package com.juemuel.trend.context;

import com.juemuel.trend.calculator.factor.Factor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class FactorContext {
    private final Map<String, Factor> factors;

    public FactorContext(Map<String, Factor> factors) {
        this.factors = factors;
    }

    public Factor get(String name) {
        return factors.get(name);
    }

    public Collection<Factor> getAll() {
        return factors.values();
    }
}
