package com.sundy.boot.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zeng.wang
 * @description redis auto config
 * @date 2019-07-01
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public JedisConnectionFactory defaultRedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(defaultRedisConnectionFactory());
        return redisTemplate;
    }
}
