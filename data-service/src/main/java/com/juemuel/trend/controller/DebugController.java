package com.juemuel.trend.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DebugController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/debug/cache")
    public String debugCache() {
        Cache cache = cacheManager.getCache("indexes");
        if (cache == null) {
            return "缓存不存在";
        }

        //tips: 荣光cacheManager获取缓存值时，需要指定缓存key的名字
        Object value = cache.get("all_codes", Object.class);
        return "缓存值: " + (value != null ? value.toString() : "null");
    }
}
