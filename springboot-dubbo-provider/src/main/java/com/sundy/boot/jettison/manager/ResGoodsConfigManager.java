package com.sundy.boot.jettison.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sundy.boot.bizManager.AbstractLocalCacheManager;
import com.sundy.boot.jettison.config.ResConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zeng.wang
 * @description 资源位货品配置管理
 */
@Slf4j
@Component
public class ResGoodsConfigManager extends AbstractLocalCacheManager<Map<String, ResConfigDTO>> {

    private static final String THREAD_NAME_PREFIX = "ResGoodsConfigLocalCache-";

    @PostConstruct
    public void initConfig() throws Exception {
        List<Map<String, ResConfigDTO>> list = load();
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception("res_goodsConfig_init_fail");
        }
    }

    /**
     * 每1分钟拉取一次配置
     */
    public ResGoodsConfigManager() {
        super(1, 60, THREAD_NAME_PREFIX);
    }

    @Override
    protected List<Map<String, ResConfigDTO>> load() {
        Map<String, ResConfigDTO> resConfigMap = Maps.newHashMap();
        log.info("resConfigMap:{}", JSON.toJSONString(resConfigMap));
        if (CollectionUtils.isEmpty(resConfigMap)) {
            return null;
        } else {
            return Lists.newArrayList(resConfigMap);
        }
    }

    @Override
    protected String traceName() {
        return "res_goodsConfig_load";
    }

    public Optional<Map<String, ResConfigDTO>> getResConfigs() {
        if (configListCache.get() != null) {
            List<Map<String, ResConfigDTO>> mapList = configListCache.get();
            if (!mapList.isEmpty()) {
                return Optional.of(configListCache.get().get(0));
            }
        }
        log.warn("getGoodsDataGson retun empty");
        return Optional.empty();
    }
}
