package com.sundy.boot.freq;

import com.alibaba.fastjson.JSONArray;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.sundy.boot.freq.BO.FreqBO;
import com.sundy.boot.freq.BO.ItemBO;
import com.sundy.boot.freq.BO.RateBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FreqStore {
    @Autowired
    @Qualifier("freqRedis")
    private JedisCluster jc;
    private static final String freqPrefix = "sundy_freq_";

    private Cache<String, Boolean> EMPTY_LOCAL_CACHE;
    private Cache<String, List<ItemBO>> ITEM_ENTITY_CACHE;
    private Cache<String, List<RateBO>> RATE_ENTITY_CACHE;

    @PostConstruct
    void init() {
        EMPTY_LOCAL_CACHE = CacheBuilder.newBuilder()
                .maximumSize(50000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();

        ITEM_ENTITY_CACHE = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
        RATE_ENTITY_CACHE = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    private List<RateBO> getRates(FreqContext context, String bizKey) {
        String key = freqPrefix + bizKey;
        if (context.isUseCache() && null != EMPTY_LOCAL_CACHE.getIfPresent(key)) {
            return null;
        }
        List<RateBO> rates = RATE_ENTITY_CACHE.getIfPresent(key);
        if (context.isUseCache() && null != rates) {
            return rates;
        } else {
            String s = jc.get(key);
            if (s != null) {
                List<RateBO> rateBOS = JSONArray.parseArray(s, RateBO.class);
                RATE_ENTITY_CACHE.put(key, rateBOS);
                return rateBOS;
            } else {
                EMPTY_LOCAL_CACHE.put(key, true);
                return null;
            }
        }
    }

    public List<FreqBO> getFreqBOS(FreqContext context, String bizKey) {
        List<RateBO> rateBOS = getRates(context, bizKey);
        List<FreqBO> freqBOS = Lists.newArrayList();
        if (rateBOS == null) {
            return freqBOS;
        }

        for (RateBO rateBO : rateBOS) {
            String key = freqPrefix + rateBO.getKey();
            List<ItemBO> itemBOS = ITEM_ENTITY_CACHE.getIfPresent(key);
            if (context.isUseCache() && null != itemBOS) {
                freqBOS.add(new FreqBO(rateBO, itemBOS));
            } else {
                String s = jc.get(key);
                if (s != null) {
                    itemBOS = JSONArray.parseArray(s, ItemBO.class);
                    ITEM_ENTITY_CACHE.put(key, itemBOS);
                    freqBOS.add(new FreqBO(rateBO, itemBOS));
                }
            }
        }

        return freqBOS;
    }
}
