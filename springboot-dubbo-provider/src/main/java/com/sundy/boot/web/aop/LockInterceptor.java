package com.sundy.boot.web.aop;

import com.sundy.boot.annotation.Lock;
import com.sundy.boot.utils.ApplicationContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCommands;

/**
 * @author plus.wang
 * @description cache切面
 * @date 2019-08-26
 */
@Aspect
@Component
public class LockInterceptor extends ApplicationContextUtil {
    private static final Logger logger = LoggerFactory.getLogger(LockInterceptor.class);
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Around("@annotation(lock)")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint, Lock lock) throws Throwable {
        RedisTemplate<String, String> redisTemplate = (RedisTemplate) getApplicationContext().getBean(lock.cache());
        String key = buildLockKey(lock.prefix(), lock.key(), proceedingJoinPoint);
        boolean acquiredLock;
        try {
            acquiredLock = lock(redisTemplate, key, lock.ttl() * 1000);
        } catch (Exception e) {
            logger.error("lock exception", e);
            acquiredLock = true;
        }
        if (!acquiredLock) {
            String msg = "illegal access for the key " + key;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        return proceedingJoinPoint.proceed();
    }

    private String getKeyFromParm(ProceedingJoinPoint proceedingJoinPoint, String key) {
        Signature signature = proceedingJoinPoint.getSignature();
        String[] parmNames = ((MethodSignature) signature).getParameterNames();
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parmNames.length; i++) {
            context.setVariable(parmNames[i], proceedingJoinPoint.getArgs()[i]);
        }
        Object value = parser.parseExpression(key).getValue(context);
        if (value instanceof String) {
            return (String) value;
        } else {
            return String.valueOf(value);
        }
    }

    private String buildLockKey(String prefix, String keySpel, ProceedingJoinPoint proceedingJoinPoint) {
        return prefix + getKeyFromParm(proceedingJoinPoint, keySpel);
    }

    private Boolean lock(RedisTemplate<String, String> redisTemplate, String key, Integer expireMilliseconds) {
        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            JedisCommands conn = (JedisCommands) connection.getNativeConnection();
            return conn.set(key, "", SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireMilliseconds);
        });
        return LOCK_SUCCESS.equals(result);
    }
}
