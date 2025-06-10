package com.juemuel.trend.source;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.pojo.IndexData;
import java.util.List;

public interface DataSource {
    /**
     * 获取指数列表
     * @return
     */
    List<Index> fetchIndexes();
    /**
     * 获取指数数据
     * @param code
     * @return
     */
    List<IndexData> fetchIndexData(String code);

    /**
     * 判断是否包含指定的指数代码
     */
    boolean isCodeValid(String code);

    /**
     * 获取当前数据源类型
     */
    String getCurrentSourceType();
}