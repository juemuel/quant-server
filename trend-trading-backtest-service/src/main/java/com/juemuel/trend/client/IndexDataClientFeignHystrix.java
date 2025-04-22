package com.juemuel.trend.client;

import java.util.List;

import com.juemuel.trend.pojo.IndexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollectionUtil;

@Component
public class IndexDataClientFeignHystrix implements IndexDataClient {

    private static final Logger log = LoggerFactory.getLogger(IndexDataClientFeignHystrix.class);

    @Override
    public List<IndexData> getIndexData(String code) {
        log.info("获取指数数据失败，服务调用异常: code={}", code);;
        IndexData indexData = new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }

}
