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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从三方获取并存入到数据库中
 */
@Service
@Slf4j
@CacheConfig(cacheNames="indexes")
public class IndexService {
    private List<Index> indexes;
    //TIPS: 实例接口的自动注入Bean的逻辑
    // 法1：实现类Bean可以通过@Primary注解，会优先注入
    // 法2：实例时可以通过@Qualifier("restDataSource")指定注入的Bean，需要和Bean的@Component的name属性值一致
    // 法3：实现类Bean可以通过@ConditionalOnProperty条件注解+配置文件中配置，当配置文件中配置了该属性时，会注入对应的Bean
    // 法4：实现类Bean可以通过@Profile注解，当配置文件中配置了该属性时（环境属性），会注入对应的Bean
    // 法5：在对应的配置类@Configuration下，通过自定义的逻辑手动注入Bean + 配合配置文件
    @Autowired
    private DataSource dataSource;

    @CacheEvict(allEntries=true)
    public void remove(){
        log.info("清除指数代码缓存");
    }

    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        log.info("存储指数代码");
        return indexes;
    }

    /**
     * 如果缓存中有 key 为 'all_codes' 的数据，则直接返回缓存；
     * 如果没有，则执行方法体并缓存结果
     * @return
     */
    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        return CollUtil.toList();
    }

    /**
     * 会清空缓存，并强制下一次调用 get() 时重新从数据源获取最新数据
     * @return
     */

    @HystrixCommand(fallbackMethod = "third_part_not_connected")
    public List<Index> fresh() {
        log.info("刷新指数代码");
        indexes = dataSource.fetchIndexes(); // 实际从数据源获取数据
        // SpringContextUtil.getBean 方式拿到的对象，会注入配置中对应的对象。 而new 出来就不会了。
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove(); // 清除缓存
        return indexService.store(); // 存入新数据到缓存
    }
    public List<Index> third_part_not_connected(){
        log.warn("数据源连接失败，返回默认数据");
        Index index = new Index();
        index.setCode("000000");
        index.setName("无效指数代码");
        return CollectionUtil.toList(index);
    }
}
