package com.sundy.boot.redis;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zeng.wang
 * @description redis分布式锁组件
 */
@Slf4j
public class RedisLock {

    private static final String OK = "OK";

    private static final String LOCK_PREFIX = "redis_common_lock:";

    private static final String SET_IF_NOT_EXIST = "NX";

    private static final String SET_WITH_EXPIRE_TIME = "EX";

    private static final String UNLOCK_LUA;

    private JedisCluster jedisCluster;

    private String lockKey;

    private String lockValue = UUID.randomUUID().toString().replaceAll("-", "") + "_" + Thread.currentThread().getId();

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    public RedisLock(JedisCluster jedisCluster, String lockKey) {
        this.jedisCluster = jedisCluster;
        this.lockKey = LOCK_PREFIX + lockKey;
    }

    public boolean lock(int seconds) {
        try {
            String result = jedisCluster.set(lockKey, lockValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds);
            if (OK.equals(result)) {
                log.info("[RedisLock.lock] 线程id={},lockKey={},lockValue={},加锁成功!时间={},seconds={}", Thread.currentThread().getId(), lockKey, lockValue, LocalDateTime.now(), seconds);
                return true;
            } else {
                log.info("[RedisLock.lock] 线程id={},lockKey={},lockValue={},加锁失败!时间={},seconds={}", Thread.currentThread().getId(), lockKey, lockValue, LocalDateTime.now(), seconds);
                return false;
            }
        } catch (Exception e) {
            log.error("[RedisLock.lock] lockKey={},lockValue={} error ", lockKey, lockValue, e);
        }
        return false;
    }

    public boolean unlock() {
        try {
            Long result = (Long) jedisCluster.eval(UNLOCK_LUA, Lists.newArrayList(lockKey), Lists.newArrayList(lockValue));
            boolean unlock = result != null && result > 0;
            if (unlock) {
                log.info("[RedisLock.unlock] lockKey={},lockValue={} 释放锁成功!时间={}", lockKey, lockValue, LocalDateTime.now());
            } else {
                log.info("[RedisLock.unlock] lockKey={},lockValue={} 释放锁失败!时间={}", lockKey, lockValue, LocalDateTime.now());
            }
            return unlock;
        } catch (Exception e) {
            log.error("[RedisLock.unlock] lockKey={},lockValue={} error ", lockKey, lockValue, e);
        }
        return false;
    }
}
