package com.juemuel.trend.source.impl;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.source.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestDataSource implements DataSource {
    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    public List<Index> fetchIndexes() {
        try {
            log.info("从REST接口获取指数列表");
            List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8131/indexes/codes.json", List.class);
            return map2Index(temp);
        } catch (Exception e) {
            log.error("获取指数列表失败: {}", e.getMessage());
            throw new RuntimeException("获取指数列表失败", e);
        }
    }
    
    @Override
    public List<IndexData> fetchIndexData(String code) {
        try {
            log.info("从REST接口获取指数数据: {}", code);
            List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8131/indexes/" + code + ".json", List.class);
            return map2IndexData(temp);
        } catch (Exception e) {
            log.error("获取指数数据失败: {}", e.getMessage());
            throw new RuntimeException("获取指数数据失败", e);
        }
    }
    
    @Override
    public String getType() {
        return "REST";
    }
    
    private List<Index> map2Index(List<Map> temp) {
        return temp.stream()
            .map(map -> {
                Index index = new Index();
                index.setCode(String.valueOf(map.get("code")));
                index.setName(String.valueOf(map.get("name")));
                return index;
            })
            .collect(Collectors.toList());
    }
    
    private List<IndexData> map2IndexData(List<Map> temp) {
        return temp.stream()
            .map(map -> {
                IndexData data = new IndexData();
                data.setDate(String.valueOf(map.get("date")));
                data.setClosePoint(Float.parseFloat(String.valueOf(map.get("closePoint"))));
                return data;
            })
            .collect(Collectors.toList());
    }
}