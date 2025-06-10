package com.juemuel.trend.client;

import java.util.Collections;
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
        log.info("获取指数数据失败，服务调用异常，进入降级逻辑: code={}", code);;
        return Collections.emptyList();
    }

}
