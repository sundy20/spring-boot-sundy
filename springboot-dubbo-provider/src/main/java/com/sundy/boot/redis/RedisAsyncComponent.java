package com.sundy.boot.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @author plus.wang
 * @description redis异步组件
 * @date 2019-06-25
 */
@Component
public class RedisAsyncComponent<K, V> {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<K, V> redisTemplate;

    @Autowired
    @Qualifier("redisExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 异步获取key
     *
     * @param k redis key
     * @return Future<V>
     */
    public Future<V> asyncGet(K k) {
        return threadPoolTaskExecutor.submit(() -> redisTemplate.opsForValue().get(k));
    }
}
