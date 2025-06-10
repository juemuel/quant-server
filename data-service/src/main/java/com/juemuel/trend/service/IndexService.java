package com.juemuel.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.source.DataSource;
import com.juemuel.trend.util.SpringContextUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 从三方获取并存入到数据库中
 */
@Service
@Slf4j
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;
    //TIPS: 定义接口变量，实际上是引用类型
    // 接口的引用，接口不能实例化对象，而接口的引用指向的是实现了接口方法的类的实例化对象
    // TODO:通过自动注入Bean的逻辑：
    // 法1：实现类Bean可以通过@Primary注解，会优先注入
    // 法2：定义接口时可以通过@Qualifier("restDataSource")指定注入的Bean，需要和Bean的@Component的name属性值一致
    // 法3：实现类Bean可以通过@ConditionalOnProperty条件注解+配置文件中配置，当配置文件中配置了该属性时，会注入对应的Bean
    // 法4：实现类Bean可以通过@Profile注解，当配置文件中配置了该属性时（环境属性），会注入对应的Bean
    // 法5：在对应的配置类@Configuration下，通过自定义的逻辑手动注入Bean + 配合配置文件
    @Autowired
    private DataSource dataSource;

    /**
     * 检查指数代码是否存在
     * @param code
     * @return
     */
    public boolean checkCodeExists(String code) {
        return dataSource.isCodeValid(code);
    }

    /**
     * 解析指数代码
     * @param input
     * @return
     */
    public List<Index> parseIndex(String input) {
        // 1. 空值校验
        if (input == null || input.trim().isEmpty()) {
            log.warn("输入为空");
            return CollUtil.toList();
        }

        // 2. 长度校验：避免超长输入导致性能问题
        int MAX_INPUT_LENGTH = 1000;
        if (input.length() > MAX_INPUT_LENGTH) {
            log.warn("输入过长: {}", input.length());
            throw new IllegalArgumentException("输入内容过长，请控制在 " + MAX_INPUT_LENGTH + " 字符以内");
        }

        // 3. 分割并去重
        List<String> tokens = Arrays.stream(input.split("\\s*,\\s*"))
                .distinct()
                .collect(Collectors.toList());

        // 4. 单个 token 的最大长度限制（如指数代码最长为6位）
        int MAX_TOKEN_LENGTH = 20;
        for (String token : tokens) {
            if (token.length() > MAX_TOKEN_LENGTH) {
                log.warn("输入项过长: {}", token);
                throw new IllegalArgumentException("输入项过长: " + token);
            }
        }

        // 5. 获取当前所有可用的 Index 列表
        //tips:必须通过代理对象调用 get()
        IndexService proxy = SpringContextUtil.getBean(IndexService.class);
        List<Index> allIndexes = proxy.get();
        if (CollUtil.isEmpty(allIndexes)) {
            log.warn("Index列表为空");
            return CollUtil.toList();
        }

        // 6. 查找匹配项：code 精确匹配，name 模糊匹配
        return tokens.stream()
                .map(token -> {
                    // 先精确匹配 code
                    for (Index index : allIndexes) {
                        if (index.getCode().equals(token)) {
                            return index;
                        }
                    }
                    // 再模糊匹配 name
                    for (Index index : allIndexes) {
                        if (index.getName().contains(token)) {
                            return index;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 数据源获取数据并存入缓存（先清空后缓存）
     * @return
     */
    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        log.info("开始刷新指数数据...");
        indexes = dataSource.fetchIndexes(); // 实际从数据源获取数据
        if (CollUtil.isEmpty(indexes)) {
            log.warn("数据源返回空列表，请检查 LocalFileDataSource 或 RestTemplate 是否正常工作");
        } else {
            log.info("成功加载 {} 个指数", indexes.size());
        }
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.removeCache(); // 清除缓存
        return indexService.storeCache(); // 存入缓存
    }
    /**
     * 存储缓存
     * @return
     */
    @CachePut("indexes#all_codes")
    public List<Index> storeCache() {
        log.info("正在写入缓存，共 {} 个指数", indexes != null ? indexes.size() : 0);
        return indexes;
    }
    /**
     * 清除缓存
     */
    @CacheEvict(allEntries=true)
    public void removeCache(){
        log.info("清除指数代码缓存");
    }

    /**
     * 如果缓存中有 key 为 'all_codes' 的数据，则直接返回缓存；
     * 如果没有，则执行方法体并缓存结果
     * @return
     */
    @Cacheable("indexes#all_codes")
    public List<Index> get(){
        log.warn("缓存未命中，返回空列表");
        return CollUtil.toList();
    }

    public List<Index> third_part_not_connected(){
        log.warn("数据源连接失败，返回默认数据");
        Index index = new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }
}
