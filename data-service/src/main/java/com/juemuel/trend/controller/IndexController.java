package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.http.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.service.IndexService;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
public class IndexController {
    @Autowired IndexService indexService;
    @Autowired
    IpConfig ipConfiguration;
//    @RequestMapping(value="/getCodes", produces={"application/xml; charset=UTF-8"})
//    @RequestMapping(value="/getCodes", produces={"application/json; charset=UTF-8"})
    @GetMapping("/freshCodes")
    public Result<List<Index>> fresh() throws Exception {
        List<Index> indexList = indexService.fresh();
        return Result.success(indexList);
    }
    @GetMapping("/getCodes")
    public Result<List<Index>> get() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort());
        List<Index> indexList = indexService.get();
        return Result.success(indexList);
    }
    @GetMapping("/removeCodes")
    public Result<String> remove() throws Exception {
        indexService.remove();
        return Result.success("remove codes successfully");
    }
}

