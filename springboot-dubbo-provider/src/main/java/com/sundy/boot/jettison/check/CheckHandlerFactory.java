package com.sundy.boot.jettison.check;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zeng.wang
 * @description 校验处理器工厂
 */
@Component
public class CheckHandlerFactory implements InitializingBean, ApplicationContextAware {

    private static final Map<String, CheckHandler> CHECK_HANDLER_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        applicationContext.getBeansOfType(CheckHandler.class).values()
                .forEach(handler -> CHECK_HANDLER_MAP.put(handler.getCheckCode(), handler));
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据校验code获取对应的处理器
     */
    public CheckHandler getHandler(String checkCode) {
        return CHECK_HANDLER_MAP.get(checkCode);
    }

    public List<CheckHandler> getHandlers(List<CheckCodeEnum> checkCodes) {
        return checkCodes.stream()
                .map(checkCodeEnum -> CHECK_HANDLER_MAP.get(checkCodeEnum.getCode()))
                .collect(Collectors.toList());
    }
}
