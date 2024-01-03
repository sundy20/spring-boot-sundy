package com.sundy.boot.inventory.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sundy.boot.inventory.domain.Freq;
import com.sundy.boot.inventory.domain.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class User {
    private JSONObject node;
    private FreqBuilder infoBuilder;
    @Getter
    private String id;
    private Long cur;
    @Getter
    @Setter
    private FreqContext context;

    @Data
    public static class AvailResult {
        private boolean avail = true;
        private String key;
        private Long oriFreq;
        private Long cnt;
        private Object fcItem;
    }

    private AvailResult buildFailResult(String key, Long cnt, Object fcItem) {
        AvailResult ret = new AvailResult();
        ret.setAvail(false);
        ret.setCnt(cnt);
        ret.setKey(key);
        ret.setFcItem(fcItem);
        return ret;
    }

    private AvailResult buildSucceResult(String key, Long cnt, Long oriFreq) {
        AvailResult ret = new AvailResult();
        ret.setAvail(true);
        ret.setOriFreq(oriFreq);
        ret.setCnt(cnt);
        ret.setKey(key);
        return ret;
    }

    private AvailResult buildSucceResult() {
        return new AvailResult();
    }

    public static User build(String data, String id, Long cur) {
        User user = new User();
        user.infoBuilder = new FreqBuilder(cur);
        user.id = id;
        user.cur = cur;
        JSONObject t = JSONObject.parseObject(data);
        if (t == null) {
            t = new JSONObject();
            t.put("freq", new JSONObject());
        } else {
            JSONObject freqNew = new JSONObject();
            Optional.ofNullable(t.getJSONObject("freq"))
                    .filter(e -> CollectionUtils.isNotEmpty(e.keySet()))
                    .ifPresent(e -> {
                        for (String key : e.keySet()) {
                            JSONObject t2 = e.getJSONObject(key);
                            if (t2.getLong("endTime") > cur) {
                                freqNew.put(key, t2);
                            }
                        }
                    });
            t.put("freq", freqNew);
        }
        t.put("last", cur);
        user.node = t;
        return user;
    }

    public AvailResult avail(Freq freq, String type) {
        List<AvailResult> availSucceResults = Lists.newArrayList();
        for (Item item1 : freq.getItemList()) {
            if (!type.equals(item1.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(item1, this.id, freq.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                if (ttl + freq.getRate().getCnt() > freqInfo.getLong("amount")) {
                    Object item = JSON.toJSON(item1);
                    return buildFailResult(freqInfo.getString("key"), ttl, item);
                } else {
                    Long overCnt = Math.max(freqInfo.getLong("amount") - ttl, 0);
                    availSucceResults.add(buildSucceResult(freqInfo.getString("key"), overCnt, freqInfo.getLong("amount")));
                }
            } else {
                availSucceResults.add(buildSucceResult(freqInfo.getString("key"), freqInfo.getLong("amount"), freqInfo.getLong("amount")));
            }
        }
        if (CollectionUtils.isEmpty(availSucceResults)) {
            return buildSucceResult();
        }
        return Collections.min(availSucceResults, Comparator.comparing(o -> o.cnt));
    }

    public void add(Freq freq) {
        for (Item item : freq.getItemList()) {
            if (!"freq".equals(item.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(item, this.id, freq.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                ttl += freq.getRate().getCnt();
                freqNode.put("ttl", ttl);
                context.getRecorder().setBizIds(this.id).setStep("freqIncr").setItem(item).record();
            } else {
                freqNode = new JSONObject();
                freqNode.put("endTime", freqInfo.getLong("endTime"));
                freqNode.put("ttl", freq.getRate().getCnt());
                context.getRecorder().setBizIds(this.id).setStep("freqInit").setItem(item).record();
            }

            this.node.getJSONObject("freq").put(freqInfo.getString("key"), freqNode);
        }
    }

    public void release(Freq freq) {
        for (Item item : freq.getItemList()) {
            if (!"freq".equals(item.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(item, this.id, freq.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                if (ttl < freq.getRate().getCnt()) {
                    ttl = 0L;
                } else {
                    ttl -= freq.getRate().getCnt();
                }

                freqNode.put("ttl", ttl);
                this.node.getJSONObject("freq").put(freqInfo.getString("key"), freqNode);
                context.getRecorder().setBizIds(this.id).setStep("freqReduce").setResult(freqInfo.getString("key")).record();
            }
        }
    }

    /**
     * 获取最长疲劳度控制时间
     */
    public int getMaxTimeToLiveSeconds() {
        long max = 0L;
        for (String key : this.node.getJSONObject("freq").keySet()) {
            // 遍历所有频次key，找出最长过期时间
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(key);
            max = Math.max(max, freqNode.getLongValue("endTime"));
        }
        // 转化为秒， 精度进一
        long ttl = (max - cur) / 1000 + 1;
        // Integer长度限制，最大支持69年, 如果ttl是负数，说明当前user bizId 下的freq 无需操作，被其他freq freqOverrun 了，默认返回1min
        if (ttl >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (ttl <= 0) {
            return 60;
        }
        return (int) ttl;
    }

    public String dump() {
        return this.node.toJSONString();
    }
}
