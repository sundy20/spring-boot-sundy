package com.sundy.boot.web.aop;

import com.sundy.boot.exception.BizException;
import com.sundy.boot.redis.RedisComponent;
import com.sundy.boot.utils.ArrayUtil;
import com.sundy.boot.web.AbstractRequest;
import com.sundy.share.enums.ResultCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


/**
 * @author plus.wang
 * @description 请求号检查切面
 * @date 2018/5/14
 */
@Aspect
@Component
public class ReqNoAop {

    private static final Logger log = LoggerFactory.getLogger(ReqNoAop.class);

    @Value("${redis.prefixReq:reqNo:}")
    private String prefixReq;

    @Value("${redis.day:1}")
    private long day;

    @Autowired
    private RedisComponent redisComponent;

    @PostConstruct
    public void init() throws Exception {
        log.info("......REQUEST-NO-CHECK init......");
    }

    @Pointcut("@annotation(com.sundy.boot.annotation.CheckReqNo)")
    public void checkRepeat() {

    }

    @Before("checkRepeat()")
    public void before(JoinPoint joinPoint) throws Exception {

        AbstractRequest request = getAbstractRequest(joinPoint);

        if (request != null) {

            final String reqNo = request.getReqNo();

            if (StringUtils.isEmpty(reqNo)) {

                throw new BizException(ResultCode.CLIENT_ERROR.getCode(), "reqNo不能为空");

            } else {

                String tempReqNo = redisComponent.get(prefixReq + reqNo);

                if ((StringUtils.isEmpty(tempReqNo))) {

                    redisComponent.set(prefixReq + reqNo, reqNo, day, TimeUnit.DAYS);

                } else {

                    if (log.isErrorEnabled()) {

                        log.error("请求号重复,reqNo = {}", reqNo);
                    }

                    throw new BizException(ResultCode.CLIENT_ERROR.getCode(), "请求号重复,reqNo=" + reqNo);
                }
            }
        }
    }

    private AbstractRequest getAbstractRequest(JoinPoint joinPoint) throws Exception {

        AbstractRequest abstractRequest = null;

        Object[] arguments = joinPoint.getArgs();

        if (ArrayUtil.isNotEmptyArray(arguments)) {

            abstractRequest = (AbstractRequest) arguments[0];
        }

        return abstractRequest;
    }
}
