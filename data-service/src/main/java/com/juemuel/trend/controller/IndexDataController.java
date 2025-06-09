package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.pojo.IndexData;
import com.juemuel.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
public class IndexDataController {
    @Autowired
    IndexDataService indexDataService;
    @Autowired
    IpConfig ipConfiguration;

//  http://127.0.0.1:8001/freshIndexData/000300
//  http://127.0.0.1:8001/getIndexData/000300
//  http://127.0.0.1:8001/removeIndexData/000300

    @GetMapping("/freshIndexData/{code}")
    public String fresh(@PathVariable("code") String code) throws Exception {
        indexDataService.fresh(code);
        return "fresh index data successfully";
    }
    @GetMapping("/getIndexData/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws Exception {
        return indexDataService.get(code);
    }
    //    @GetMapping("/data/{code}")
    //    public List<IndexData> get(@PathVariable("code") String code) throws Exception {
    //        System.out.println("[controller]port:" + ipConfiguration.getPort());
    //        return indexDataService.get(code);
    //    }
    @GetMapping("/removeIndexData/{code}")
    public String remove(@PathVariable("code") String code) throws Exception {
        indexDataService.remove(code);
        return "remove index data successfully";
    }
}