package com.sundy.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author zeng.wang
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
}
