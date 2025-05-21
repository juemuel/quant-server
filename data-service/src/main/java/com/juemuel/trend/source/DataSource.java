package com.juemuel.trend.source;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.pojo.IndexData;
import java.util.List;

public interface DataSource {
    List<Index> fetchIndexes();
    List<IndexData> fetchIndexData(String code);
    String getType();
}