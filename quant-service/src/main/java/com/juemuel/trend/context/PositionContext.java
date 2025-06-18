package com.juemuel.trend.context;

import com.juemuel.trend.calculator.position.PositionManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class PositionContext {
    private final Map<String, PositionManager> positionManagers;

    public PositionContext(Map<String, PositionManager> positionManagers) {
        this.positionManagers = positionManagers;
    }

    public PositionManager get(String name) {
        return positionManagers.get(name);
    }

    public Collection<PositionManager> getAll() {
        return positionManagers.values();
    }
}
