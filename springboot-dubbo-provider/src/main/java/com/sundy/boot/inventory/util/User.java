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

    public AvailResult avail(Freq freqBO, String type) {
        List<AvailResult> availSucceResults = Lists.newArrayList();
        for (Item itemBO : freqBO.getItemList()) {
            if (!type.equals(itemBO.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(itemBO, this.id, freqBO.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                if (ttl + freqBO.getRate().getCnt() > freqInfo.getLong("amount")) {
                    Object item = JSON.toJSON(itemBO);
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

    public void add(Freq freqBO) {
        for (Item itemBO : freqBO.getItemList()) {
            if (!"freq".equals(itemBO.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(itemBO, this.id, freqBO.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                ttl += freqBO.getRate().getCnt();
                freqNode.put("ttl", ttl);
                context.getRecorder().setBizIds(this.id).setStep("freqIncr").setItemBO(itemBO).record();
            } else {
                freqNode = new JSONObject();
                freqNode.put("endTime", freqInfo.getLong("endTime"));
                freqNode.put("ttl", freqBO.getRate().getCnt());
                context.getRecorder().setBizIds(this.id).setStep("freqInit").setItemBO(itemBO).record();
            }

            this.node.getJSONObject("freq").put(freqInfo.getString("key"), freqNode);
        }
    }

    public void release(Freq freqBO) {
        for (Item itemBO : freqBO.getItemList()) {
            if (!"freq".equals(itemBO.getType())) {
                continue;
            }
            JSONObject freqInfo = infoBuilder.fetchFreqInfo(itemBO, this.id, freqBO.getRate().getKey());
            JSONObject freqNode = this.node.getJSONObject("freq").getJSONObject(freqInfo.getString("key"));
            if (freqNode != null && cur < freqNode.getLong("endTime")) {
                Long ttl = freqNode.getLong("ttl");
                if (ttl < freqBO.getRate().getCnt()) {
                    ttl = 0L;
                } else {
                    ttl -= freqBO.getRate().getCnt();
                }

                freqNode.put("ttl", ttl);
                this.node.getJSONObject("freq").put(freqInfo.getString("key"), freqNode);
                context.getRecorder().setBizIds(this.id).setStep("freqReduce").setResult(freqInfo.getString("key")).record();
            }
        }
    }

    public String dump() {
        return this.node.toJSONString();
    }
}
