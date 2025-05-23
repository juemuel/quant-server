package com.juemuel.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

// http://127.0.0.1:8001/getCodes
@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableHystrix
@EnableCaching
public class DataServiceApplication
{
    public static void main( String[] args )
    {
        int port = 8001;
        int eurekaServerPort = 8761;
        int redisPort = 6379;
//        int dataSourcePort = 8111;

        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }
        if (NetUtil.isUsableLocalPort(redisPort)){
            System.err.printf("检查到端口%d 未启用，判断 redis 服务器没有启动，本服务无法使用，故退出%n", redisPort );
            System.exit(1);
        }
//        if(NetUtil.isUsableLocalPort(dataSourcePort)) {
//            System.err.printf("检查到端口%d 未启用，判断静态数据服务没有启动，本服务无法使用，故退出%n", dataSourcePort );
//            System.exit(1);
//        }

        if(null!=args && 0!=args.length) {
            for (String arg : args) {
                if(arg.startsWith("port=")) {
                    String strPort= StrUtil.subAfter(arg, "port=", true);
                    if(NumberUtil.isNumber(strPort)) {
                        port = Convert.toInt(strPort);
                    }
                }
            }
        }

        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(DataServiceApplication.class).properties("server.port=" + port).run(args);

    }
}
