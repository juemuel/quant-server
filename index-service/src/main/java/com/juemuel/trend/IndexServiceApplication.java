package com.juemuel.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TODO: 计划调整为DATA-SERVICE
 * 包括本地存储
 * 考虑改写成tushare、理杏仁
 * http://127.0.0.1:8111/indexes/codes.json
 * http://127.0.0.1:8111/indexes/000300.json
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCaching
public class IndexServiceApplication {
    public static void main(String[] args) {
        int port = 0;
        int defaultPort = 8111;
        int defaultPortMax = defaultPort + 9; // 尝试的最大端口范围
        int redisPort = 6379;
        int eurekaServerPort = 8761;
        if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort );
            System.exit(1);
        }

        if(NetUtil.isUsableLocalPort(redisPort)) {
            System.err.printf("检查到端口%d 未启用，判断 redis 服务器没有启动，本服务无法使用，故退出%n", redisPort );
            System.exit(1);
        }

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

        if(0==port) {
            Future<Integer> future = ThreadUtil.execAsync(() ->{
                int p = 0;
                System.out.printf("请于5秒钟内输入端口号, 推荐  %d ,超过5秒将默认使用 %d %n",defaultPort,defaultPort);
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    String strPort = scanner.nextLine();
                    if(!NumberUtil.isInteger(strPort)) {
                        System.err.println("只能是数字");
                        continue;
                    }
                    else {
                        p = Convert.toInt(strPort);
                        scanner.close();
                        break;
                    }
                }
                return p;
            });
            try{
                port=future.get(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e){
                port = defaultPort;
            }
        }
        port = findAvailablePort(port, defaultPortMax);

        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port );
            System.exit(1);
        }
        System.out.println("服务将在端口 " + port + " 启动...");
        new SpringApplicationBuilder(IndexServiceApplication.class).properties("server.port=" + port).run(args);
    }
    /**
     * 寻找第一个可用的端口
     * @param startPort 开始搜索的端口
     * @param maxPort 搜索的最大端口
     * @return 可用端口
     */
    private static int findAvailablePort(int startPort, int maxPort) {
        for (int i = startPort; i < maxPort; i++) {
            try (ServerSocket socket = new ServerSocket(i)) {
                return i; // 端口可用
            } catch (IOException e) {
                // 端口不可用，尝试下一个
            }
        }
        throw new IllegalStateException("无法找到可用端口，已尝试至 " + maxPort);
    }
}