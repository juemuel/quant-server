package com.juemuel.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul网关中
 * CORS放行（Access-Control-Allow-Origin）、
 * 路由转发（application.yml）
 * 权限拦截控制发生在具体的微服务层，需要配置CORS拦截器
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableDiscoveryClient
public class ZuulApplication {
    public static void main(String[] args) {
        int port = 8031;
        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(ZuulApplication.class).properties("server.port=" + port).run(args);
    }
}
