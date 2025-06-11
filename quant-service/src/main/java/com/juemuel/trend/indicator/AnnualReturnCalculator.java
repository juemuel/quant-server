package com.juemuel.trend.indicator;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.pojo.AnnualProfit;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.Trade;
import com.juemuel.trend.service.BackTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AnnualReturnCalculator implements IndicatorCalculator {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);

    @Override
    public String getName() {
        return "annual_return";
    }

    @Override
    public Object calculate(List<IndexData> data, List<Trade> trades, Map<String, Object> params) {
        float initCash = 1000;
        float finalValue = trades.isEmpty() ? initCash : trades.get(trades.size()-1).getRate() * initCash;
        float years = getYears(data);
        float rate = (finalValue - initCash) / initCash;
        float annualRate = years > 0 ? (float)Math.pow(1 + rate, 1/years) - 1 : 0;

        // 扩展支持按年统计
        List<AnnualProfit> annualProfits = caculateAnnualProfits(data, trades);
        Map<String, Object> result = new HashMap<>();
        result.put("total", rate);
        result.put("annual", annualRate);
        result.put("annualProfits", annualProfits);
        return result;
    }
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Trade> trades) {
        // 实现类似 BackTestService 的年度利润计算逻辑
        // 或者调用 backTestService.getIndexIncome(year, indexDatas) 等方法
        return new ArrayList<>();
    }
    /**
     * 计算当前的时间范围有多少年（计算起始年份和结束年份）
     * @param allIndexDatas
     * @return
     */
    public float getYears(List<IndexData> allIndexDatas) {
        if (allIndexDatas == null || allIndexDatas.isEmpty()) {
            log.warn("无法计算年份：数据为空");
            return 0; // 返回默认值避免异常
        }
        try {
            String sDateStart = allIndexDatas.get(0).getDate();
            String sDateEnd = allIndexDatas.get(allIndexDatas.size() - 1).getDate();

            if (StrUtil.isBlankOrUndefined(sDateStart) || StrUtil.isBlankOrUndefined(sDateEnd)) {
                log.warn("日期字段为空，无法计算年份");
                return 0;
            }

            Date dateStart = DateUtil.parse(sDateStart);
            Date dateEnd = DateUtil.parse(sDateEnd);

            long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
            return days / 365f;
        } catch (Exception e) {
            log.error("计算年份时发生错误", e);
            return 0;
        }
    }

}
