package com.sundy.boot.freq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
public class FreqContext {
    JSONObject jsonObject = new JSONObject();
    Set<String> emptyConfigKeys = Sets.newHashSet();
    Set<String> stockFcKeys = Sets.newHashSet();
    Set<String> freqFcKeys = Sets.newHashSet();
    Set<String> fcBizIds = Sets.newHashSet();
    Map<String, Object> freqItemFcKeyMap = Maps.newHashMap();
    Map<String, Object> stockItemFcKeyMap = Maps.newHashMap();
    JSONArray freqPassResults = new JSONArray();
    JSONArray stockPassResults = new JSONArray();


    FreqRecorder recorder = new FreqRecorder();
    boolean useCache;
    boolean isOk;

    public FreqContext(String name, String bizId, boolean useCache) {
        this.isOk = true;
        this.recorder.setAction(name);
        this.recorder.setBizIds(Lists.newArrayList(bizId));
        this.useCache = useCache;
    }

    public void save(String bizKey, boolean fc) {
        this.jsonObject.put(bizKey, !fc);
        this.isOk = this.isOk && !fc;
    }

    public void addExtra(JSONArray extra) {
        JSONArray jsonArray = Optional.ofNullable(jsonObject.getJSONArray("extra"))
                .orElseGet(() -> new JSONArray());
        jsonArray.addAll(extra);
        jsonObject.put("extra", jsonArray);
    }

    public void addExtra(JSONObject extra) {
        JSONArray jsonArray = Optional.ofNullable(jsonObject.getJSONArray("extra"))
                .orElseGet(() -> new JSONArray());
        jsonArray.add(extra);
        jsonObject.put("extra", jsonArray);
    }

    public void saveEmptyConfigKey(String bizKey) {
        emptyConfigKeys.add(bizKey);
    }

    public void saveFreqItemFcKeyMap(String bizKey, Object item) {
        freqItemFcKeyMap.put(bizKey, item);
    }

    public void saveStockItemFcKeyMap(String bizKey, Object item) {
        stockItemFcKeyMap.put(bizKey, item);
    }

    public void saveStockFcKey(String bizKey) {
        stockFcKeys.add(bizKey);
    }

    public void saveFreqFcKey(String bizKey) {
        freqFcKeys.add(bizKey);
    }

    public void saveFcBizId(String bizId) {
        fcBizIds.add(bizId);
    }

    public void saveRreqPassResult(JSONObject result) {
        freqPassResults.add(result);
    }

    public void saveStockPassResult(JSONObject result) {
        stockPassResults.add(result);
    }

    public JSONObject dump() {
        this.jsonObject.put("OK", isOk);
        this.jsonObject.put("emptyConfigKeys", emptyConfigKeys);
        this.jsonObject.put("stockFcKeys", stockFcKeys);
        this.jsonObject.put("freqFcKeys", freqFcKeys);
        this.jsonObject.put("fcBizIds", fcBizIds);
        this.jsonObject.put("freqItemFcKeyMap", freqItemFcKeyMap);
        this.jsonObject.put("stockItemFcKeyMap", stockItemFcKeyMap);
        this.jsonObject.put("freqPassResults", freqPassResults);
        this.jsonObject.put("stockPassResults", stockPassResults);

        return this.jsonObject;
    }
}
