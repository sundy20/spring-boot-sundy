package com.sundy.boot.bizManager.impl;

import com.sundy.boot.bizManager.AbstractLocalCacheManager;
import com.sundy.boot.bizManager.FuturesSubscribeBiz;
import com.sundy.boot.domain.FuturesSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author plus.wang
 * @description 本地缓存实现demo
 * @date 2019-09-02
 */
@Slf4j
@Component
public class FuturesSubscribeManager extends AbstractLocalCacheManager<FuturesSubscribe> {
    private static final String THREAD_NAME_PREFIX = "FuturesSubscribeLocalCache-";
    @Autowired
    private FuturesSubscribeBiz futuresSubscribeBiz;

    public FuturesSubscribeManager() {
        super(1, 300, THREAD_NAME_PREFIX);
    }

    @Override
    protected List<FuturesSubscribe> load() {
        return futuresSubscribeBiz.findAll();
    }

    public FuturesSubscribe getById(Long id) {
        Assert.notNull(id, "FuturesSubscribeManager_getById id is null");
        if (configListCache.get() == null || id == 0) {
            return null;
        }
        return configListCache.get().stream().filter(futuresSubscribe -> futuresSubscribe.getId().equals(id)).findAny().orElse(null);
    }

    @Override
    protected String traceName() {
        return "FuturesSubscribeManager";
    }
}
