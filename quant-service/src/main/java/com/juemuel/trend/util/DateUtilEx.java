package com.juemuel.trend.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.controller.BackTestController;
import com.juemuel.trend.pojo.IndexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class DateUtilEx {
    private static final Logger log = LoggerFactory.getLogger(DateUtilEx.class);
    /**
     * 计算时间跨度的年数（基于天数）
     * @param startDate 起始日期字符串
     * @param endDate   结束日期字符串
     * @return 年数
     */
    public static float getYears(String startDate, String endDate) {
        if (StrUtil.isBlankOrUndefined(startDate) || StrUtil.isBlankOrUndefined(endDate)) {
            log.warn("日期字段为空，无法计算年份");
            return 0;
        }
        try {
            Date dateStart = DateUtil.parse(startDate);
            Date dateEnd = DateUtil.parse(endDate);
            long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
            return days / 365f;
        } catch (Exception e) {
            log.error("计算年份时发生错误", e);
            return 0;
        }
    }


    /**
     * 基于 indexDatas 提取起止日期并计算年数
     * @param indexDatas
     * @return 年数
     */
    public static float getYearsFromIndexDatas(List<IndexData> indexDatas) {
        if (indexDatas == null || indexDatas.isEmpty()) {
            log.warn("无法计算年份：数据为空");
            return 0;
        }
        String sDateStart = indexDatas.get(0).getDate();
        String sDateEnd = indexDatas.get(indexDatas.size() - 1).getDate();
        return getYears(sDateStart, sDateEnd);
    }
    /**
     * 从日期字符串中提取年份
     * @param dateStr
     * @return
     */
    public static int extractYear(String dateStr) {
        return Integer.parseInt(dateStr.split("-")[0]);
    }
}
