package com.juemuel.trend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.source.DataSource;
import com.juemuel.trend.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;

@Service
@Slf4j
@CacheConfig(cacheNames="index_datas")
public class IndexDataService {
    // 替换为数据源
    // private Map<String, List<IndexData>> indexDatas=new HashMap<>();
    // @Autowired RestTemplate restTemplate;
    private Map<String, List<IndexData>> indexDatas = new HashMap<>();
    
    @Autowired
    private DataSource dataSource;  // 注入数据源

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<IndexData> fresh(String code) {
        log.info("刷新指数数据: {}", code);
        List<IndexData> indexData = dataSource.fetchIndexData(code);
        indexDatas.put(code, indexData);
        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }
   /**
     * 从缓存中移除指数数据
     */
    @CacheEvict(key="'indexData-code-'+ #p0")
    public void remove(String code) {
        log.info("移除指数数据缓存: {}", code);
    }
    /**
     * 存储指数数据到缓存
     */
    @CachePut(key="'indexData-code-'+ #p0")
    public List<IndexData> store(String code) {
        log.info("存储指数数据到缓存: {}", code);
        return indexDatas.get(code);
    }
    /**
     * 获取指数数据
     */
    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code) {
        log.info("获取指数数据: {}", code);
        return CollUtil.toList();
    }
    /**
     * 熔断后的降级处理
     */
    public List<IndexData> third_part_not_connected(String code) {
        log.warn("数据源连接失败，返回默认数据: {}", code);
        IndexData index = new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }
}