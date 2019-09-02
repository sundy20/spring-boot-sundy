package com.sundy.boot.biz.impl;

import com.sundy.boot.biz.FuturesSubscribeBiz;
import com.sundy.boot.biz.LocalCacheManager;
import com.sundy.boot.domain.FuturesSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author zeng.wang
 * @description 本地缓存实现demo
 * @date 2019-09-02
 */
@Slf4j
@Component
public class FuturesSubscribeManager extends LocalCacheManager<FuturesSubscribe> {

    @Autowired
    private FuturesSubscribeBiz futuresSubscribeBiz;

    public FuturesSubscribeManager() {
        super(1, 300);
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
