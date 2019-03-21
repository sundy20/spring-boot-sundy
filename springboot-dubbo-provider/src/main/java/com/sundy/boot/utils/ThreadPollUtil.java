package com.sundy.boot.utils;

import java.util.concurrent.*;

/**
 * @author plus.wang
 * @description 自定义线程池
 * @date 2018/5/15
 */
public class ThreadPollUtil {

    public static ThreadPoolExecutor defineThreadPool(int coreSize, String namePrefix) {

        return new ThreadPoolExecutor(coreSize, coreSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
                new NamedThreadFactory(namePrefix), new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolExecutor defineThreadPool(int coreSize, String namePrefix, long keepAliveTime,
                                                      TimeUnit timeUnit, int queueSize,
                                                      ThreadPoolExecutor.AbortPolicy abortPolicy) {

        return new ThreadPoolExecutor(coreSize, coreSize, keepAliveTime, timeUnit, new LinkedBlockingQueue<>(queueSize),
                new NamedThreadFactory(namePrefix), abortPolicy);
    }

    public static ScheduledThreadPoolExecutor defineScheduledThreadPool(int coreSize, String namePrefix) {

        return new ScheduledThreadPoolExecutor(coreSize, new NamedThreadFactory(namePrefix, true));
    }
}
