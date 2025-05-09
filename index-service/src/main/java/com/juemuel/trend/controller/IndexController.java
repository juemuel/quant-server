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
//  http://127.0.0.1:8111/getCodes

    @GetMapping("/getCodes")
    @CrossOrigin
    public List<Index> getCodes() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort());
        //TODO:现在get实现好像只能从缓存取了？
        return indexService.get();
    }
}