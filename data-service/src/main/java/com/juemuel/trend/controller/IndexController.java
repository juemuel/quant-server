package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.service.IndexService;

@RestController
public class IndexController {
    @Autowired IndexService indexService;
    @Autowired
    IpConfig ipConfiguration;
//    @RequestMapping(value="/getCodes", produces={"application/xml; charset=UTF-8"})
//    @RequestMapping(value="/getCodes", produces={"application/json; charset=UTF-8"})
    @GetMapping("/freshCodes")
    public List<Index> fresh() throws Exception {
        return indexService.fresh();
    }
    @GetMapping("/getCodes")
    public List<Index> get() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort());
        return indexService.get();
    }
    @GetMapping("/removeCodes")
    public String remove() throws Exception {
        indexService.remove();
        return "remove codes successfully";
    }
}

