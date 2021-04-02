package com.sundy.boot.jettison.goods;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 货品处理器工厂
 */
@Component
public class GoodsHandlerFactory implements InitializingBean, ApplicationContextAware {

    private static final Map<String, GoodsHandler> GOODS_HANDLER_MAP = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        //将Spring容器中所有的GoodsHandler注册到GOODS_HANDLER_MAP
        applicationContext.getBeansOfType(GoodsHandler.class).values()
                .forEach(handler -> GOODS_HANDLER_MAP.put(handler.getGoodsType(), handler));
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据货品类型获取对应的处理器
     */
    public GoodsHandler getHandler(String goodsType) {
        return GOODS_HANDLER_MAP.get(goodsType);
    }
}
