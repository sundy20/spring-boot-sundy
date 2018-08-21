package com.sundy.boot.web.filter;

import com.sundy.boot.limiter.injvm.JvmTPSLimiter;
import com.sundy.boot.utils.WebUtil;
import com.sundy.share.enums.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务限流 处理
 * 待修改为基于redis的分布式限流器
 */
@Component
public class JvmTPSLimiterFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JvmTPSLimiterFilter.class);

    private final JvmTPSLimiter jvmTPSLimiter = new JvmTPSLimiter();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void setRate(int rate) {
        jvmTPSLimiter.setRate(rate);
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        //定义一个小时超时清除限流map中的会话URI
        executorService.scheduleAtFixedRate(() -> {

            jvmTPSLimiter.timerClear();

            if (log.isInfoEnabled()) {

                log.info("after clear request stats size: {}", jvmTPSLimiter.getStats().size());
            }
        }, 0, 60, TimeUnit.MINUTES);

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        HttpServletResponse response = (HttpServletResponse) resp;

        limitFilter(request, response, chain);

    }

    private void limitFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

        String sessionIdAndReqURI = req.getRequestedSessionId() + ":" + req.getRequestURI();

        if (!jvmTPSLimiter.isAllowable(sessionIdAndReqURI)) {

            if (log.isWarnEnabled()) {

                log.warn("request sessionId:{} requestURI:{} frequently", req.getRequestedSessionId(), req.getRequestURI());
            }

            WebUtil.sendError(Integer.valueOf(ResultCode.CLIENT_ERROR.getCode()), "request frequently!", resp);

            return;
        }

        chain.doFilter(req, resp);
    }

}
