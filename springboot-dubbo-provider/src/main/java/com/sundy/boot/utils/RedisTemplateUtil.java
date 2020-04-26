package com.sundy.boot.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zeng.wang
 * @description RedisTemplate 工具类
 * @date 2020/4/26
 */
public class RedisTemplateUtil {

    public static void batchSet(RedisTemplate<String, String> redisTemplate, Map<String, String> kvMap, TimeUnit unit, int timeout) {
        /* 批量插入多条数据 */
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                kvMap.forEach((k, v) -> redisTemplate.opsForValue().set(k, v, timeout, unit));
                return null;
            }
        });
    }

    public static List<String> batchGet(RedisTemplate<String, String> redisTemplate, List<String> keyList) {
        /* 批量获取多条数据 */
        List<Object> objects = redisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
            keyList.forEach(stringRedisConnection::get);
            return null;
        });
        return objects.stream().map(String::valueOf).collect(Collectors.toList());
    }
}
