package com.sundy.boot.inventory.dao;

import com.alibaba.fastjson.JSONArray;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.sundy.boot.inventory.domain.Freq;
import com.sundy.boot.inventory.domain.Item;
import com.sundy.boot.inventory.domain.Rate;
import com.sundy.boot.inventory.util.Constants;
import com.sundy.boot.inventory.util.FreqContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 库存频次数据层
 */
@Slf4j
@Component
public class FreqDAO {

    @Autowired
    @Qualifier("inventoryRedis")
    private JedisCluster jedisCluster;

    private Cache<String, Boolean> emptyLocalCache;

    private Cache<String, List<Rate>> rateEntityCache;

    private Cache<String, List<Item>> itemEntityCache;

    @PostConstruct
    void init() {
        emptyLocalCache = CacheBuilder.newBuilder()
                .maximumSize(50000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .build();

        itemEntityCache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
        rateEntityCache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    public List<Freq> getFreqs(FreqContext context, String bizKey) {
        List<Rate> rates = getRates(context, bizKey);
        List<Freq> freqs = Lists.newArrayList();
        if (rates == null) {
            return freqs;
        }
        for (Rate rate : rates) {
            String key = Constants.FREQ_ITEM_PREFIX + rate.getKey();
            List<Item> items = itemEntityCache.getIfPresent(key);
            if (context.isUseCache() && null != items) {
                freqs.add(new Freq(rate, items));
            } else {
                String s = jedisCluster.get(key);
                if (s != null) {
                    items = JSONArray.parseArray(s, Item.class);
                    itemEntityCache.put(key, items);
                    freqs.add(new Freq(rate, items));
                }
            }
        }
        return freqs;
    }

    private List<Rate> getRates(FreqContext context, String bizKey) {
        String key = Constants.FREQ_RATE_PREFIX + bizKey;
        if (context.isUseCache() && null != emptyLocalCache.getIfPresent(key)) {
            return null;
        }
        List<Rate> rates = rateEntityCache.getIfPresent(key);
        if (context.isUseCache() && null != rates) {
            return rates;
        } else {
            String s = jedisCluster.get(key);
            if (s != null) {
                List<Rate> rateList = JSONArray.parseArray(s, Rate.class);
                rateEntityCache.put(key, rateList);
                return rateList;
            } else {
                emptyLocalCache.put(key, true);
                return null;
            }
        }
    }
}
