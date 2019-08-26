package com.sundy.boot.biz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zeng.wang
 * @description 本地内存抽象管理
 * @date 2019-08-26
 */
@Slf4j
public abstract class LocalCacheManager<T> implements ApplicationListener<ContextRefreshedEvent> {
    protected final AtomicReference<List<T>> configListCache = new AtomicReference<>();
    protected final int poolSize;
    protected final int delay;

    public LocalCacheManager(int poolSize, int delay) {
        this.poolSize = poolSize;
        this.delay = delay;
    }

    /**
     * periodic refresher thread pool
     */
    private ScheduledExecutorService executorService;

    /**
     * refresh the sales
     */
    public synchronized void refresh() {
        try {
            List<T> configs = load();
            if (configs != null) {
                configListCache.set(configs);
            }
        } catch (Exception e) {
            log.error("{} cache refresh", traceName(), e);
            throw e;
        }
    }

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
        //make sure we only execute for once even get multiple event
        if (configListCache.get() == null) {
            try {
                refresh();
                executorService = Executors.newScheduledThreadPool(poolSize);
                executorService.scheduleWithFixedDelay(this::refresh, delay, delay, TimeUnit.SECONDS);
                log.info("sales cache initialized");
            } catch (Exception e) {
                log.error("sales cache exception", e);
                throw new RuntimeException("sales cache fail to initialized");
            }
        } else {
            log.info("sales cache already initialed");
        }
    }

    abstract protected List<T> load();

    abstract protected String traceName();
}
