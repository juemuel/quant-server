package com.juemuel.trend.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class) // 确保在Redis自动配置之后加载
public class CoreAutoConfiguration {

    @Bean
    public IpConfig ipConfig() {
        return new IpConfig();
    }

    @Bean
    @ConditionalOnClass(RedisConnectionFactory.class)
    public RedisCacheConfig redisCacheConfig() {
        return new RedisCacheConfig();
    }
}