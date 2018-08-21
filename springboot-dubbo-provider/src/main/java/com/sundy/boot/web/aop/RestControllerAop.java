package com.sundy.boot.web.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sundy.boot.utils.ArrayUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author plus
 * @date 2017/12/19
 */
@Aspect
@Component
public class RestControllerAop {

    private static final Logger log = LoggerFactory.getLogger(RestControllerAop.class);

    @Around("execution(* com.sundy.boot.web.rest.*.*(..))")
    public Object around(ProceedingJoinPoint pjp) {

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        log.info("URL : " + request.getRequestURL().toString());

        log.info("HTTP_METHOD : " + request.getMethod());

        log.info("IP : " + request.getRemoteAddr());

        Object[] objects = pjp.getArgs();

        Enumeration<String> enu = request.getParameterNames();

        StringBuilder stringBuilder = new StringBuilder();

        while (enu.hasMoreElements()) {

            String paraName = enu.nextElement();

            stringBuilder.append(paraName).append(" : ").append(request.getParameter(paraName)).append(" ");
        }

        if (log.isInfoEnabled()) {

            if (ArrayUtil.isNotEmptyArray(objects)) {

                log.info("---------- 请求 RestController： 【 " + pjp.getTarget().getClass().getName() + " 】  方法:  【 " + pjp.getSignature().getName() + " 】  参数： 【 " + stringBuilder.toString() + " 】   begin ----------");

            } else {

                log.info("---------- 请求 RestController： 【 " + pjp.getTarget().getClass().getName() + " 】  方法:  【 " + pjp.getSignature().getName() + " 】    begin ----------");
            }
        }

        long begin = System.currentTimeMillis();

        //执行目标方法
        Object result = null;

        try {

            result = pjp.proceed();

        } catch (Throwable throwable) {

            log.error(pjp.getTarget().getClass().getName() + " : " + pjp.getSignature().getName() + " error : " + throwable.getMessage(), throwable);
        }

        if (log.isInfoEnabled()) {

            log.info("---------- 请求 RestController： 【 " + pjp.getTarget().getClass().getName() + " 】  方法:  【 " + pjp.getSignature().getName() + " 】   end ---------- time used: 【 " + (System.currentTimeMillis() - begin) + " ms 】" + " return 【 " + JSON.toJSONString(result, SerializerFeature.WriteMapNullValue) + " " + "】");
        }

        return result;
    }
}