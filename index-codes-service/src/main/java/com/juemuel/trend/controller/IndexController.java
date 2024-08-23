package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    IpConfig ipConfiguration;
//  注解：允许跨域
//  http://127.0.0.1:8011/codes

    @GetMapping("/codes")
    @CrossOrigin
    public List<Index> codes() throws Exception {
        System.out.println("current instance's port is "+ ipConfiguration.getPort());
        return indexService.get();
    }
}