package com.sundy.boot.web.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sundy.boot.annotation.Cache;
import com.sundy.boot.utils.ApplicationContextUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.wang
 * @description cache切面
 * @date 2019-08-26
 */
@Aspect
public class CacheInterceptor extends ApplicationContextUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);
    private final ObjectMapper mapper;
    /**
     * flow control parameters.
     */
    private volatile int flowControl = 20;
    private volatile int flowControlWait = 50;
    protected volatile Semaphore flowSemaphore;

    public CacheInterceptor() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @PostConstruct
    public void init() {
        flowSemaphore = new Semaphore(flowControl);
    }

    @Around("@annotation(cache)")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint, Cache cache) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        Class returnType = ((MethodSignature) signature).getReturnType();
        RedisTemplate<String, String> redisTemplate = (RedisTemplate) getApplicationContext().getBean(cache.cache());
        String key = cache.key();
        try {
            String jsonValue = null;
            if (cache.waitTimeout() == 0 || StringUtils.isEmpty(cache.asyncExecutor())) {
                jsonValue = redisTemplate.boundValueOps(key).get();
            } else {
                Future<String> jsonFuture = null;
                Object executorObject = getApplicationContext().getBean(cache.asyncExecutor());
                if (executorObject instanceof ExecutorService) {
                    ExecutorService executorService = (ExecutorService) executorObject;
                    jsonFuture = executorService.submit(() -> redisTemplate.boundValueOps(key).get());
                } else if (executorObject instanceof ThreadPoolTaskExecutor) {
                    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executorObject;
                    jsonFuture = taskExecutor.submit(() -> redisTemplate.boundValueOps(key).get());
                }
                if (jsonFuture != null) {
                    try {
                        jsonValue = jsonFuture.get(cache.waitTimeout(), TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        logger.error("async get cache error", e);
                    }
                }
            }
            if (jsonValue != null) {
                if ("null".equals(jsonValue) && cache.cacheNull()) {
                    return null;
                }
                return mapper.readValue(jsonValue, returnType);
            }
        } catch (Exception e) {
            logger.error("cache fetch error", e);
        }
        if (flowControl > 0) {
            if (!flowSemaphore.tryAcquire(1, flowControlWait, TimeUnit.MILLISECONDS)) {
                throw new Exception("flow is over control");
            }
        }
        try {
            Object r = proceedingJoinPoint.proceed();
            try {
                if (r != null || cache.cacheNull()) {
                    String v = mapper.writeValueAsString(r);
                    redisTemplate.boundValueOps(key).set(v, getTtlSeconds(cache.ttl()), TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                logger.error("cache set error", e);
            }
            return r;
        } finally {
            if (flowControl > 0) {
                flowSemaphore.release();
            }
        }
    }

    protected int getTtlSeconds(int ttlSeconds) {
        return ttlSeconds + (int) ((double) ttlSeconds * Math.random() * 0.5D);
    }

    public void setFlowControlWait(int flowControlWait) {
        this.flowControlWait = flowControlWait;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }
}
