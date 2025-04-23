package com.juemuel.trend.controller;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class IndexDataController {
    @Autowired
    IndexDataService indexDataService;
    @Autowired
    IpConfig ipConfiguration;

//  http://127.0.0.1:8111/data/000300

    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort());
        return indexDataService.get(code);
    }
}