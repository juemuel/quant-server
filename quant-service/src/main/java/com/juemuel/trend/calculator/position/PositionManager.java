package com.juemuel.trend.calculator.position;

import com.juemuel.trend.pojo.IndexData;

/**
 * 仓位管理接口
 */
public interface PositionManager {
    float calculatePosition(float availableCash, IndexData currentData);
}
