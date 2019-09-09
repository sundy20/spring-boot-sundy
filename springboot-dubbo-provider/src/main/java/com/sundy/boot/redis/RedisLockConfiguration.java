package com.sundy.boot.redis;

import com.sundy.boot.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author plus.wang
 * @description redis分布式锁配置
 * @date 2019-06-14
 */
@Configuration
public class RedisLockConfiguration {

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, Constants.REDIS_LOCK_PREFIX,
                Constants.REDIS_LOCK_EXPIRE_MS);
    }
}
