package com.juemuel.trend.web;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RefreshScope // 加上该注解配合配置中心动态刷新配置，请求端口号/actuator/refresh，可动态刷新; 配合公司的gitlab的webhooks每次上传完，主动调用内网地址即可。（一个client端适合）
public class ViewController {
    @Value("${version}")
    String version;
//    Model用于视图层和控制层传数据
    @GetMapping("/")
    public String view(Model m) throws Exception {
        m.addAttribute("version", version);
        return "view";
    }
}