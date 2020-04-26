package com.sundy.boot.web.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sundy.boot.annotation.MultiCache;
import com.sundy.boot.utils.ApplicationContextUtil;
import com.sundy.boot.utils.AsyncUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author zeng.wang
 * @description 批量缓存拦截器
 * @date 2020/4/24
 */
@Aspect
@Component
public class MultiCacheInterceptor extends ApplicationContextUtil {

    private static final Logger logger = LoggerFactory.getLogger(MultiCacheInterceptor.class);

    private final ObjectMapper objectMapper;

    public MultiCacheInterceptor() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Around("@annotation(multiCache)")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint, MultiCache multiCache) throws Throwable {
        String prefix = multiCache.prefix();
        Object[] args = proceedingJoinPoint.getArgs();
        List<String> keyList = new ArrayList<>();
        List param = null;
        for (Object o : args) {
            if (o.getClass().isAssignableFrom(List.class)) {
                param = (List) o;
                param.forEach(o1 -> {
                    keyList.add(buildKey(prefix, String.valueOf(o1)));
                });
                break;
            }
        }
        RedisTemplate<String, String> redisTemplate = getBean(multiCache.cache());
        try {
            List<String> jsonValueList = null;
            String asyncExecutor = multiCache.asyncExecutor();
            if (multiCache.waitTimeout() == 0 || StringUtils.isEmpty(asyncExecutor)) {
                jsonValueList = redisTemplate.opsForValue().multiGet(keyList);
            } else {
                Future<List<String>> jsonFuture = null;
                Object executorObject = getApplicationContext().getBean(asyncExecutor);
                if (executorObject instanceof ExecutorService) {
                    ExecutorService executorService = (ExecutorService) executorObject;
                    jsonFuture = AsyncUtil.runCallable(executorService, () -> redisTemplate.opsForValue().multiGet(keyList));
                } else if (executorObject instanceof ThreadPoolTaskExecutor) {
                    ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executorObject;
                    jsonFuture = AsyncUtil.runCallable(taskExecutor, () -> redisTemplate.opsForValue().multiGet(keyList));
                }
                if (jsonFuture != null) {
                    try {
                        jsonValueList = jsonFuture.get(multiCache.waitTimeout(), TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        logger.error("multiGet cache error", e);
                    }
                }
            }
            if (jsonValueList != null) {
                if (jsonValueList.isEmpty()) {
                    return null;
                }
                Map map = new HashMap();
                Signature signature = proceedingJoinPoint.getSignature();
                Class returnType = ((MethodSignature) signature).getReturnType();
                if (returnType.isAssignableFrom(Map.class)) {
                    Class modelType = multiCache.modelType();
                    String property = multiCache.property();
                    jsonValueList.forEach(s -> {
                        try {
                            Object o = objectMapper.readValue(s, modelType);
                            Field field = o.getClass().getDeclaredField(property);
                            field.setAccessible(true);
                            String key = field.get(property).toString();
                            map.put(key, o);
                        } catch (Exception e) {
                            logger.error("multi cache objectMapper readValue error", e);
                        }
                    });
                } else {
                    throw new RuntimeException("multi cache returnType does not match");
                }
                if (map.size() == keyList.size()) {
                    return map;
                } else {
                    param.removeAll(map.keySet());
                    Map map1 = (Map) getObject(proceedingJoinPoint, multiCache, prefix, redisTemplate);
                    map.putAll(map1);
                }
                return map;
            }
        } catch (Exception e) {
            logger.error("multiGet cache fetch error", e);
        }
        return getObject(proceedingJoinPoint, multiCache, prefix, redisTemplate);
    }

    private Object getObject(ProceedingJoinPoint proceedingJoinPoint, MultiCache multiCache, String prefix, RedisTemplate<String, String> redisTemplate) throws Throwable {
        Object r = proceedingJoinPoint.proceed();
        try {
            if (r != null) {
                if (r instanceof Map) {
                    Map map = (Map) r;
                    map.forEach((k, v) -> {
                        String key = buildKey(prefix, String.valueOf(k));
                        try {
                            String json = objectMapper.writeValueAsString(v);
                            redisTemplate.opsForValue().set(key, json, getTtlSeconds(multiCache.configKey(), multiCache.defaultTtl()), TimeUnit.SECONDS);
                        } catch (Exception e) {
                            logger.error("multi cache objectMapper writeValueAsString error", e);
                        }
                    });
                } else {
                    throw new RuntimeException("multi cache returnType does not match");
                }
            }
        } catch (Exception e) {
            logger.error("multi cache set error", e);
        }
        return r;
    }

    private String buildKey(String prefix, String key) {
        return prefix + "_" + key;
    }


    private int getTtlSeconds(String configKey, int defaultTimeOut) {
        int ttl = defaultTimeOut;//配置中心获取 TODO
        return ttl + (int) (ttl * Math.random() * 0.5);
    }

}
