package com.sundy.boot.config;

import com.sundy.boot.web.aop.CacheInterceptor;
import com.sundy.boot.web.aop.LockInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author plus.wang
 * @description spring线程池配置
 * @date 2019-06-25
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * redis 异步操作线程池
     */
    @Bean(value = "redisExecutor")
    public ThreadPoolTaskExecutor redisThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //两倍于cpu核心数线程 针对io密集型 线程默认超时60秒
        int threadCount = Runtime.getRuntime().availableProcessors() << 1;
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(1024);
        executor.setThreadNamePrefix("redisExecutor-");
        executor.setAwaitTerminationSeconds(threadCount);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean
    public CacheInterceptor cacheInterceptor() {
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        //500 * 20 = 1w qps
        cacheInterceptor.setFlowControl(500);
        //100 ms
        cacheInterceptor.setFlowControlWait(100);
        return cacheInterceptor;
    }

    @Bean
    public LockInterceptor lockInterceptor() {
        return new LockInterceptor();
    }

    /**
     * 资源位数据异步操作线程池
     */
    @Bean(value = "resGoodsExecutor")
    public ThreadPoolTaskExecutor resGoodsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int threadCount = Runtime.getRuntime().availableProcessors() << 1;
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(128);
        executor.setThreadNamePrefix("resGoodsExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(threadCount);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    /**
     * 货品数据异步操作线程池
     */
    @Bean(value = "goodsDataExecutor")
    public ThreadPoolTaskExecutor goodsDataExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int threadCount = Runtime.getRuntime().availableProcessors() << 1;
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("goodsDataExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(threadCount);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
