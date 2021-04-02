package com.sundy.boot.jettison.recall;

import com.sundy.boot.jettison.bo.RecallBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 召回处理器工厂
 */
@Component
public class RecallHandlerFactory implements InitializingBean, ApplicationContextAware {

    private static final Map<String, RecallHandler<RecallBO>> RECALL_HANDLER_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        //将Spring容器中所有的RecallHandler注册到RECALL_HANDLER_MAP
        applicationContext.getBeansOfType(RecallHandler.class).values()
                .forEach(handler -> RECALL_HANDLER_MAP.put(handler.getRecallCode(), handler));
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据货品类型获取对应的处理器
     */
    public RecallHandler<RecallBO> getHandler(String recallCode) {
        return RECALL_HANDLER_MAP.get(recallCode);
    }
}
