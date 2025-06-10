package com.juemuel.trend.controller;

import java.util.List;

import com.juemuel.trend.config.IpConfig;
import com.juemuel.trend.http.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 刷新指数代码
     * @return
     * @throws Exception
     */
    @GetMapping("/freshCodes")
    public Result<List<Index>> fresh() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort() + " --- /freshCodes");
        List<Index> indexList = indexService.fresh();
        return Result.success(indexList);
    }

    /**
     *  获取指数代码
     * @return
     * @throws Exception
     */
    @GetMapping("/getCodes")
    public Result<List<Index>> get() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort() + " --- /getCodes");
        List<Index> indexList = indexService.get();
        return Result.success(indexList);
    }

    /**
     * 删除指数代码缓存
     * @return
     * @throws Exception
     */
    @GetMapping("/removeCodes")
    public Result<String> remove() throws Exception {
        System.out.println("[controller]port:" + ipConfiguration.getPort() + " --- /removeCodes");
        indexService.removeCache();
        return Result.success("remove codes successfully");
    }

    /**
     * 解析指数代码字符串
     * @param codesOrNames 解析代码字符串
     * @return
     */
    @GetMapping("/parseIndex")
    public Result<List<Index>> parseIndex(@RequestParam("codesOrNames") String codesOrNames) {
        System.out.println("[controller]port:" + ipConfiguration.getPort() + " --- /parseIndex");
        List<Index> result = indexService.parseIndex(codesOrNames);
        return Result.success(result);
    }

}

