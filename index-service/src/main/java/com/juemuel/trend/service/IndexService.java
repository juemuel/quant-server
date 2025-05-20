package com.juemuel.trend.service;

import java.util.List;

import com.juemuel.trend.pojo.Index;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;

@Service
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;

    /**
     * 如果缓存中有 key 为 'all_codes' 的数据，则直接返回缓存；
     * 如果没有，则执行方法体并缓存结果
     * @return
     */
    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        Index index = new Index();
        index.setName("无效指数代码");
        index.setCode("000000");
        return CollUtil.toList(index);
    }
}