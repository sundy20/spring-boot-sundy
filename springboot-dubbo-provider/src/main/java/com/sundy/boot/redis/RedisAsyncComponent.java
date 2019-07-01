package com.sundy.boot.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @author zeng.wang
 * @description redis异步组件
 * @date 2019-06-25
 */
@Component
public class RedisAsyncComponent<K, V> {

    @Autowired
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
        return threadPoolTaskExecutor.submit(() -> redisTemplate.execute((RedisCallback<V>) redisConnection -> {
            byte[] bytes = redisConnection.get(getKeyBytes(k));
            return deserializeValue(bytes);
        }));
    }

    private byte[] getKeyBytes(K k) {
        return ((RedisSerializer<K>) redisTemplate.getKeySerializer()).serialize(k);
    }

    private V deserializeValue(byte[] bytes) {
        return ((RedisSerializer<V>) redisTemplate.getValueSerializer()).deserialize(bytes);
    }
}
