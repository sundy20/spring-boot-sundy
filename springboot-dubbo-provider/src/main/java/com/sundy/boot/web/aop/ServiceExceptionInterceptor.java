package com.sundy.boot.web.aop;

import com.sundy.share.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author plus.wang
 * @description 服务层异常答应切面
 * @date 2019-08-26
 */
@Aspect
@Component
@Slf4j
public class ServiceExceptionInterceptor {
    /**
     * 设置切入点
     */
    @Around("execution(* com.sundy.boot.dubbo.provider.*.*(..))")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Exception e) {
            String clazzName = proceedingJoinPoint.getTarget().getClass().getCanonicalName();
            String methodName = proceedingJoinPoint.getSignature().getName();
            Signature signature = proceedingJoinPoint.getSignature();
            Class returnType = ((MethodSignature) signature).getReturnType();
            Object returnObject = returnType.newInstance();
            if (returnObject instanceof Result) {
                Result result = Result.failure(e.getMessage());
                log.info("[{}:{}] return error code {}, msg {}", clazzName, methodName, result.getMsgCode(),
                        result.getMsgInfo());
                return returnObject;
            } else {
                log.error("exception not expected", e);
                throw e;
            }
        }
    }
}
