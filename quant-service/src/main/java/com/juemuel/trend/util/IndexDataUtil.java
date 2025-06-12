package com.juemuel.trend.util;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.pojo.IndicatorData;

import java.util.ArrayList;
import java.util.List;

public class IndexDataUtil {

    /**
     * 提取所有 MA 值
     * @param indicatorDatas 指标数据列表
     * @return MA 值列表
     */
    public static List<Float> extractMas(List<IndicatorData> indicatorDatas) {
        List<Float> mas = new ArrayList<>();
        for (IndicatorData data : indicatorDatas) {
            if (data.getMa() != null) {  // 使用 IndicatorData 的 getMa()
                mas.add(data.getMa());
            }
        }
        return mas;
    }


    /**
     * 计算MA
     * @param currentIndex 当前索引位置
     * @param ma 移动平均周期
     * @param indexDatas 数据列表
     * @return 计算结果
     */
    public static float getMA(int currentIndex, int ma, List<IndexData> indexDatas) {
        if (currentIndex < ma - 1) {
            return 0;
        }
        float sum = 0;
        for (int i = currentIndex - ma + 1; i <= currentIndex; i++) {
            sum += indexDatas.get(i).getClosePoint();
        }
        return sum / ma;
    }

}
