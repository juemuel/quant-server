package com.juemuel.trend.client;

import java.util.List;

import com.juemuel.trend.pojo.IndexData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 访问不了的时候，就去找 IndexDataClientFeignHystrix 要数据
@Primary
@FeignClient(value = "DATA-SERVICE",fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    // 需要和数据服务提供者的Controller方法名一致
    @GetMapping("/getIndexData/{code}")
    public List<IndexData> getIndexData(@PathVariable("code") String code);
}