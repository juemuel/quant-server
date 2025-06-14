package com.juemuel.trend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.source.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cn.hutool.core.collection.CollectionUtil;

/**
 * 指数数据服务
 * 首次访问：从数据源获取，然后存入 Redis 和内存
 * 后续访问：优先从 Redis 缓存获取
 * 定时任务：定时执行都会重新获取文件并更新缓存
 */
@Service
@Slf4j
@CacheConfig(cacheNames="index_datas")
public class IndexDataService {
    private Map<String, List<IndexData>> indexDatas = new HashMap<>();
    @Autowired
    private DataSource dataSource;  // 注入数据源

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    @CachePut(key="'indexData-'+ #p0 + '-' + #p1")  // 添加缓存注解到主方法
    public List<IndexData> fresh(String market, String code) {
        remove(market, code);  // 先清除旧缓存
        List<IndexData> indexData = dataSource.fetchIndexData(market, code);
        if (indexData == null || indexData.isEmpty()) {
            log.warn("定时获取指数为空: {}", market+code);
            return third_part_not_connected(market, code);
        }
        indexDatas.put(code, indexData);
        log.info("定时刷新指数成功: {}", market+code);
        return indexData;  // 直接返回数据，不再调用store
    }

   /**
     * 从缓存中移除指数数据
     */
    @CacheEvict(key="'indexData-'+ #p0 + '-' + #p1")
    public void remove(String market, String code) {
        log.info("移除指数数据缓存: {}", market+code);
    }
    /**
     * 获取指数数据
     */
    @Cacheable(key="'indexData-'+ #p0 + '-' + #p1")
    public List<IndexData> get(String market, String code) {
        log.info("缓存未命中，从数据源获取指数数据: {}", market+code);
        List<IndexData> data = dataSource.fetchIndexData(market, code);
        if (data == null || data.isEmpty()) {
            log.warn("数据源返回空数据: {}", market+code);
            return third_part_not_connected(market, code);
        }
        return data;
    }
    /**
     * 熔断后的降级处理
     */
    public List<IndexData> third_part_not_connected(String market, String code) {
        log.warn("数据源连接失败，返回默认数据: {}", market+code);
        IndexData index = new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }
}