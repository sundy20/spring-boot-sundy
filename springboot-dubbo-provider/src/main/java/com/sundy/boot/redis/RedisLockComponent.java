package com.sundy.boot.redis;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * @author plus.wang
 * @description redis分布式锁组件
 * @date 2019-06-14
 */
@Component
public class RedisLockComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockComponent.class);
    private static final String OK = "OK";
    private static final String NOT_EXIST = "NX";
    private static final String EXPIRED = "PX";

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    private ThreadLocal<String> lockFlag = new ThreadLocal<>();

    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    public boolean lock(String lockKey, int milliseconds) {
        try {
            String uuid = UUID.randomUUID().toString() + Thread.currentThread().getId();
            lockFlag.set(uuid);
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                JedisCommands jedisCommands = (JedisCommands) connection.getNativeConnection();
                return jedisCommands.set(lockKey, uuid, NOT_EXIST, EXPIRED, milliseconds);
            });
            if (OK.equals(result)) {
                LOGGER.info("[RedisLockComponent.lock] 线程id:{},lockKey:{},lockValue:{},加锁成功!时间:{},milliseconds={}", Thread.currentThread().getId(), lockKey, uuid, LocalTime.now(), milliseconds);
                return true;
            } else {
                LOGGER.info("[RedisLockComponent.lock] 线程id={},lockKey={},lockValue={},加锁失败!时间={},milliseconds={}", Thread.currentThread().getId(), lockKey, uuid, LocalDateTime.now(), milliseconds);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("[RedisLockComponent.lock] lockKey={} error ", lockKey, e);
        }
        return false;
    }

    public boolean unlock(String lockKey) {
        try {
            // 使用Lua脚本删除Redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, Lists.newArrayList(lockKey),
                            Lists.newArrayList(lockFlag.get()));
                }
                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, Lists.newArrayList(lockKey),
                            Lists.newArrayList(lockFlag.get()));
                }
                return 0L;
            });
            return result != null && result > 0;
        } catch (Exception e) {
            LOGGER.error("[RedisLockComponent.unlock] lockKey:{},lockValue:{} error ", lockKey, lockFlag.get(), e);
        }
        return false;
    }
}
