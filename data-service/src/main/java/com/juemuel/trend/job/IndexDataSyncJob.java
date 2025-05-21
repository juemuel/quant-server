package com.juemuel.trend.job;
import java.util.List;
import cn.hutool.core.date.DateUtil;
import com.juemuel.trend.pojo.Index;
import com.juemuel.trend.service.IndexDataService;
import com.juemuel.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IndexDataSyncJob {

    @Autowired
    private IndexService indexService;

    @Autowired
    private IndexDataService indexDataService;

    @Scheduled(cron = "0 */1 * * * ?")
    protected void executeInternal() {
        System.out.println("定时启动：" + DateUtil.now());
        List<Index> indexes = indexService.fresh();
        for (Index index : indexes) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束：" + DateUtil.now());
    }
}