package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.http.Result;
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

    /**
     * 刷新指定指数代码的数据，并返回结果状态
     */
    @GetMapping("/freshIndexData/{code}")
    public Result<String> fresh(@PathVariable("code") String code) throws Exception {
        indexDataService.fresh(code);
        return Result.success("fresh index data successfully");
    }
    /**
     * 获取指定指数代码的数据并封装为 Result 返回
     */
    @GetMapping("/getIndexData/{code}")
    public Result<List<IndexData>> get(@PathVariable("code") String code) throws Exception {
        List<IndexData> data = indexDataService.get(code);
        if (data == null || data.isEmpty()) {
            return Result.error(404, "指数数据为空");
        }
        return Result.success(data);
    }
    /**
     * 移除指定指数代码的缓存数据并返回结果状态
     */
    @GetMapping("/removeIndexData/{code}")
    public Result<String> remove(@PathVariable("code") String code) throws Exception {
        indexDataService.remove(code);
        return Result.success("remove index data successfully");
    }
}