package com.juemuel.trend;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 *
 */
@Configuration
public class CommonCoreApplication
{
    public static void main(String[] args) {
        int port = 8;
        int eurekaServerPort = 8761;
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }

        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(CommonCoreApplication.class).properties("server.port=" + port).run(args);

    }
}
