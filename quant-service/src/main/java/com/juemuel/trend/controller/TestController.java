package com.juemuel.trend.controller;

import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.pojo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    IndexDataClient indexDataClient;

    @GetMapping("/test/{code}")
    public List<IndexData> test(@PathVariable String code) {
        return indexDataClient.getIndexData(code);
    }
}

