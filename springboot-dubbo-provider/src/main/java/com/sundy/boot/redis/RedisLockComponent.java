package com.sundy.boot.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author zeng.wang
 * @description redis分布式锁组件
 * @date 2019-06-14
 */
@Component
public class RedisLockComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockComponent.class);

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    public boolean lock(String lockKey, int milliseconds) throws InterruptedException {
        LOGGER.info("lock lockKey:{},milliseconds:{}", lockKey, milliseconds);
        Lock lock = redisLockRegistry.obtain(lockKey);
        return lock.tryLock(milliseconds, TimeUnit.MILLISECONDS);
    }

    public void unlock(String lockKey) {
        LOGGER.info("unlock lockKey:{}", lockKey);
        Lock lock = redisLockRegistry.obtain(lockKey);
        lock.unlock();
    }
}
