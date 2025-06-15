package com.juemuel.trend.calculator.position;

import com.juemuel.trend.pojo.IndexData;
import org.springframework.stereotype.Component;

/**
 * 固定金额买入策略
 */
@Component("fixed_position")
public class FixedPositionManager implements PositionManager {

    private float fixedAmount = 1000f; // 每次买入固定金额

    @Override
    public float calculatePosition(float availableCash, IndexData currentData) {
        float amountToUse = Math.min(fixedAmount, availableCash);
        return amountToUse / currentData.getClosePoint(); // 计算可买入股数
    }
}
