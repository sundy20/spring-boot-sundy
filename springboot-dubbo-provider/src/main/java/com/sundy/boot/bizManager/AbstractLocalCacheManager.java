package com.sundy.boot.bizManager;

import com.sundy.boot.utils.ThreadPollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author plus.wang
 * @description 本地内存抽象管理
 * @date 2019-08-26
 */
@Slf4j
public abstract class AbstractLocalCacheManager<T> implements ApplicationListener<ContextRefreshedEvent> {
    protected final AtomicReference<List<T>> configListCache = new AtomicReference<>();
    protected final String threadNamePrefix;
    protected final int poolSize;
    protected final int delay;

    public AbstractLocalCacheManager(int poolSize, int delay, String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        this.poolSize = poolSize;
        this.delay = delay;
    }

    /**
     * periodic refresher thread pool
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * refresh the data size控制256 考虑内存溢出
     */
    public synchronized void refresh() {
        try {
            List<T> configs = load();
            List<T> subConfigs = configs.subList(0, Math.min(configs.size(), 256));
            if (CollectionUtils.isNotEmpty(subConfigs)) {
                configListCache.set(subConfigs);
            }
        } catch (Throwable e) {
            log.error("{} cache refresh", traceName(), e);
        }
    }

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
        //make sure we only execute for once even get multiple event
        if (configListCache.get() == null) {
            try {
                refresh();
                scheduledExecutorService = ThreadPollUtil.defineScheduledThreadPool(poolSize, threadNamePrefix);
                scheduledExecutorService.scheduleWithFixedDelay(this::refresh, delay, delay, TimeUnit.SECONDS);
                log.info("{} local cache initialized", traceName());
            } catch (Exception e) {
                log.error("{} local cache exception", traceName(), e);
                throw new RuntimeException(traceName() + " local cache fail to initialized");
            }
        } else {
            log.info("{} local cache already initialed", traceName());
        }
    }

    abstract protected List<T> load();

    abstract protected String traceName();
}
