package com.juemuel.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.source.DataSource;
import com.juemuel.trend.util.SpringContextUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从三方获取并存入到数据库中
 */
@Service
@Slf4j
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;
    // 封装数据源
    // @Autowired RestTemplate restTemplate;
    // public List<Index> fetch_indexes_from_third_part(){
    //     List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8131/indexes/codes.json",List.class);
    //     return map2Index(temp);
    // }
    @Autowired
    private DataSource dataSource;  // 注入数据源

    @CacheEvict(allEntries=true)
    public void remove(){
        log.info("清除指数代码缓存");
    }

    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        log.info("存储指数代码");
        return indexes;
    }

    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        return CollUtil.toList();
    }

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        log.info("刷新指数代码");
        indexes = dataSource.fetchIndexes();
        // SpringContextUtil.getBean 方式拿到的对象，会注入配置中对应的对象。 而new 出来就不会了。
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }
    public List<Index> third_part_not_connected(){
        log.warn("数据源连接失败，返回默认数据");
        Index index = new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }
}
