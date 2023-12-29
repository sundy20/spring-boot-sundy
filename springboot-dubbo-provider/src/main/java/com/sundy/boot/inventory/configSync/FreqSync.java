package com.sundy.boot.inventory.configSync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sundy.boot.inventory.DO.ItemDO;
import com.sundy.boot.inventory.dao.ItemDAO;
import com.sundy.boot.inventory.domain.Rate;
import com.sundy.boot.inventory.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FreqSync {

    @Resource
    private ItemDAO itemDAO;

    @Autowired
    @Qualifier("inventoryRedis")
    private JedisCluster jedisCluster;

    public void sync(Set<Long> nids) {
        List<ItemDO> itemDOList = itemDAO.getItemDOs(nids);
        Map<Long, ItemDO> itemDOMap = itemDOList.stream().collect(Collectors.toMap(ItemDO::getNid, Function.identity()));
        for (Long nid : nids) {
            try {
                if (!itemDOMap.containsKey(nid)) {
                    delFreq(nid);
                } else {
                    ItemDO freqItemDO = itemDOMap.get(nid);
                    addFreq(freqItemDO.getNid(), freqItemDO.getAttribute());
                }
            } catch (Exception e) {
                log.error("sync single freq error freqId={}", nid, e);
            }
        }
    }

    private void delFreq(Long nid) {
        String rateKey = Constants.FREQ_RATE_PREFIX + nid;
        Long ret = jedisCluster.del(rateKey);
        log.info("del freq_rate! rateKey:{},ret{}", rateKey, ret);
        String itemKey = Constants.FREQ_ITEM_PREFIX + nid;
        ret = jedisCluster.del(itemKey);
        log.info("del freq_item! itemKey:{},ret{}", itemKey, ret);
    }

    private void addFreq(Long nid, Map<String, Object> attribute) {
        if (attribute != null && attribute.containsKey(Constants.FREQ_EXT)) {
            JSONObject freqExt = (JSONObject) JSON.toJSON(attribute.get(Constants.FREQ_EXT));
            if (freqExt.containsKey("freqType") && "1".equalsIgnoreCase(freqExt.getString("freqType"))) {
                Rate rateBO = new Rate();
                rateBO.setKey(freqExt.getString("relationFreq"));
                rateBO.setCnt(1L);
                String rateKey = Constants.FREQ_RATE_PREFIX + nid;
                //TODO key ttl 设置
                String ret = jedisCluster.set(rateKey, JSON.toJSONString(Collections.singletonList(rateBO)));
                log.info("set freq_rate! rateKey:{},ret{}", rateKey, ret);
                String itemKey = Constants.FREQ_ITEM_PREFIX + nid;
                Long delRet = jedisCluster.del(itemKey);
                log.info("del freq_item! itemKey:{},ret{}", itemKey, delRet);
            } else {
                addOriginFreqItem(nid, attribute);
            }

        } else {
            addOriginFreqItem(nid, attribute);
        }
    }

    private void addOriginFreqItem(Long nid, Map<String, Object> attribute) {
        Optional.ofNullable(attribute)
                .map(e -> e.get(Constants.FREQ))
                .map(Object::toString)
                .filter(StringUtils::isNotEmpty)
                .map(JSONArray::parseArray)
                .filter(e -> !e.isEmpty())
                .map(e -> {
                    JSONArray itemBOS = new JSONArray();
                    for (int i = 0; i < e.size(); ++i) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.putAll(e.getJSONObject(i));
                        if ("action_range".equals(jsonObject.getString("unit")) && null != attribute.get("endTime")) {
                            jsonObject.put("endTime", attribute.get("endTime"));
                        }
                        itemBOS.add(jsonObject);
                    }
                    String itemKey = Constants.FREQ_ITEM_PREFIX + nid;
                    //TODO key ttl 设置
                    String ret = jedisCluster.set(itemKey, JSON.toJSONString(itemBOS));
                    log.info("set freq_item! itemKey:{},ret{}", itemKey, ret);
                    Rate rateBO = new Rate();
                    rateBO.setKey(nid.toString());
                    rateBO.setCnt(1L);
                    String rateKey = Constants.FREQ_RATE_PREFIX + nid;
                    //TODO key ttl 设置
                    ret = jedisCluster.set(rateKey, JSON.toJSONString(Collections.singletonList(rateBO)));
                    log.info("set freq_rate! rateKey:{},ret{}", rateKey, ret);
                    return true;
                }).orElseGet(() -> {
                    delFreq(nid);
                    return false;
                });
    }
}
