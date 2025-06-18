package com.juemuel.trend.context;

import com.juemuel.trend.calculator.signal.SignalCondition;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * 交易信号判断器的注册中心/上下文容器
 */
@Service
public class SignalContext {
    private final Map<String, SignalCondition> signalConditions;

    public SignalContext(Map<String, SignalCondition> signalConditions) {
        this.signalConditions = signalConditions;
        System.out.println("Loaded signal conditions: " + signalConditions.keySet());
    }

    public SignalCondition get(String name) {
        return signalConditions.get(name);
    }

    public Collection<SignalCondition> getAll() {
        return signalConditions.values();
    }
}
