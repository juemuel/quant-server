package com.juemuel.trend.source.impl;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.source.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary // 标记为默认注入的Bean
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
            String url = "http://127.0.0.1:8131/indexes/" + code + ".json";
            log.info("从REST接口获取指数数据, URL: {}", url);

            // 先检查资源是否存在
            try {
                ResponseEntity<String> checkResponse = restTemplate.getForEntity(url, String.class);
                if (checkResponse.getStatusCode() != HttpStatus.OK) {
                    log.error("指数数据文件不存在: {}", url);
                    return null;
                }
            } catch (HttpClientErrorException.NotFound e) {
                log.error("指数数据文件不存在: {}", url);
                return null;
            }
            List<Map> temp = restTemplate.getForObject(url, List.class);
//            log.info("获取到原始数据: {}", temp);  // 添加日志查看原始数据
            List<IndexData> result = map2IndexData(temp);
//            log.info("转换后的数据: {}", result);  // 添加日志查看转换后的数据
            return result;
        } catch (Exception e) {
            log.error("获取指数数据失败: {}, 错误类型: {}, 错误信息: {}",
                    code, e.getClass().getName(), e.getMessage());
            return null;
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