package com.sundy.boot.freq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sundy.boot.exception.BizException;
import com.sundy.boot.exception.StockLimitException;
import com.sundy.boot.freq.BO.FreqBO;
import com.sundy.boot.freq.BO.ItemBO;
import com.sundy.boot.utils.AsyncUtil;
import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.FreqQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FreqManager {
    @Autowired
    @Qualifier("freqRedis")
    private JedisCluster jc;

    @Autowired
    private FreqStore store;

    @Qualifier("freqExecutor")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    private static final String freqPrefix = "sundy_freq_";

    public JSONObject availFreqByKey(FreqQuery query, Long cur, List<FreqBO> freqBoList, String bizKey, FreqContext context) {
        FreqRecorder recorder = new FreqRecorder();
        recorder.setAction("availFreq").setBizIds(query.getBizIds()).setBizCode(bizKey);
        JSONObject ret = new JSONObject();
        ret.put("bizKey", bizKey);
        ret.put("fc", false);
        boolean dump = Optional.ofNullable(query.getExtra()).map(e -> e.containsKey("dump")).orElse(false);
        try {
            FreqInfoBuilder freqInfoBuilder = new FreqInfoBuilder(cur);
            for (FreqBO freqBO : freqBoList) {
                List<ItemBO> itemBoList = freqBO.getItemBOList();
                for (ItemBO itemBO : itemBoList) {
                    if (!"stock".equals(itemBO.getType())) {
                        continue;
                    }

                    JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, query.getBizIds().get(0), freqBO.getRateBO().getKey());
                    String cnt = jc.get(freqInfo.getString("key"));

                    if (dump) {
                        JSONArray extra = Optional.ofNullable(ret.getJSONArray("extra"))
                                .orElseGet(JSONArray::new);
                        JSONObject t = new JSONObject();
                        t.put(freqInfo.getString("key"), cnt);
                        t.put("elapse", jc.ttl(freqInfo.getString("key")));
                        extra.add(t);
                        ret.put("extra", extra);
                    }
                    Long remain = Optional.ofNullable(cnt)
                            .map(Long::valueOf)
                            .map(e -> itemBO.getAmount() - e)
                            .orElse(itemBO.getAmount());
                    recorder.setStep(remain.toString()).setItemBO(itemBO).record();

                    if (cnt != null && Integer.parseInt(cnt) + freqBO.getRateBO().getCnt() > itemBO.getAmount()) {
                        ret.put("fc", true);
                        ret.put("isStockFcKey", true);
                        ret.put("stockFcData", JSON.toJSON(itemBO));
                        return ret;

                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bizKey", bizKey);
                    jsonObject.put("overCnt", remain);
                    jsonObject.put("oriStock", itemBO.getAmount());
                    context.saveStockPassResult(jsonObject);
                }
            }

        } catch (Exception e) {
            log.error("availFreqByKey exception:{}", ExceptionUtils.getFullStackTrace(e));
        }
        return ret;
    }

    protected List<User> fetchUsers(Long cur, String bizId, List<String> bisIds) {
        return fetchUsers(cur, bizId, bisIds, null);
    }

    protected List<User> fetchUsers(Long cur, String bizId, List<String> bisIds, FreqContext context) {
        List<User> users = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(bisIds)) {
            Set<String> idSet = Sets.newHashSet();
            if (StringUtils.isNotBlank(bizId)) {
                idSet.add(bizId);
            }
            idSet.addAll(bisIds);

            List<Future<User>> ayncResults = Lists.newArrayList();
            for (String s : idSet) {
                ayncResults.add(AsyncUtil.asyncCall(executor, () -> {
                    String node = jc.get(freqPrefix + s);
                    User user = User.build(node, s, cur);
                    user.setContext(context);
                    return user;
                }));
            }
            users.addAll(ayncResults.stream().map(e -> {
                try {
                    return e.get();
                } catch (Exception exception) {
                    log.error("freq fetchUsers redis timeout ", exception);
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet()));
        } else {
            String node = jc.get(freqPrefix + bizId);
            User user = User.build(node, bizId, cur);
            user.setContext(context);
            users.add(user);
        }

        return users;
    }

    public Result<JSONObject> availFreq(FreqQuery query) {
        FreqContext context = new FreqContext("availFreq", query.getBizIds().get(0),
                Optional.ofNullable(query.getExtra())
                        .map(e -> e.get("useCache"))
                        .map(Object::toString)
                        .map(Boolean::valueOf)
                        .orElse(true));
        Long cur = System.currentTimeMillis();
        boolean dump = Optional.ofNullable(query.getExtra()).map(e -> e.containsKey("dump")).orElse(false);
        List<User> users = fetchUsers(cur, query.getBizIds().get(0), query.getBizIds());
        List<Future<JSONObject>> ayncResults = Lists.newArrayList();
        Map<String, Future<List<FreqBO>>> map = Maps.newHashMap();
        for (String bizKey : query.getBizKeys()) {
            Future<List<FreqBO>> future = AsyncUtil.asyncCall(executor, () -> this.store.getFreqBOS(context, bizKey));
            map.put(bizKey, future);
        }

        for (Map.Entry<String, Future<List<FreqBO>>> entry : map.entrySet()) {
            String bizKey = entry.getKey();
            Future<List<FreqBO>> listFuture = entry.getValue();
            List<FreqBO> freqBoList = AsyncUtil.futureGet(listFuture, 30, TimeUnit.MILLISECONDS, null);
            if (Objects.isNull(freqBoList)) {
                throw new BizException(String.format("bizKey：%s库存频次redis获取超时", bizKey));
            }
            if (CollectionUtils.isEmpty(freqBoList)) {
                context.saveEmptyConfigKey(bizKey);
                continue;
            }
            boolean haveStock = false;
            for (FreqBO freqBO : freqBoList) {
                for (User u : users) {
                    User.AvailResult availResult = u.avail(freqBO, "freq");
                    if (!availResult.isAvail()) {
                        context.saveFreqFcKey(bizKey);
                        context.saveFcBizId(u.getId());
                        context.saveFreqItemFcKeyMap(bizKey, availResult.getFcItem());
                        context.save(bizKey, true);
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("bizId", u.getId());
                        jsonObject.put("bizKey", bizKey);
                        jsonObject.put("overCnt", availResult.getCnt());
                        jsonObject.put("oriFreq", availResult.getOriFreq());
                        context.saveRreqPassResult(jsonObject);
                    }
                }
                List<ItemBO> itemBoList = freqBO.getItemBOList();
                for (ItemBO itemBO : itemBoList) {
                    if ("stock".equals(itemBO.getType())) {
                        haveStock = true;
                        break;
                    }
                }
            }
            if (haveStock) {
                ayncResults.add(AsyncUtil.asyncCall(executor, () -> availFreqByKey(query, cur, freqBoList, bizKey, context)));
            }
        }

        for (Future<JSONObject> future : ayncResults) {
            try {
                JSONObject t = future.get();
                if (dump) {
                    context.addExtra(t.getJSONArray("extra"));
                }
                if (BooleanUtils.isTrue(t.getBoolean("isStockFcKey"))) {
                    context.saveStockFcKey(t.getString("bizKey"));
                    context.saveStockItemFcKeyMap(t.getString("bizKey"), t.get("stockFcData"));
                }
                if (BooleanUtils.isTrue(t.getBoolean("isFreqFcKey"))) {
                    context.saveFreqFcKey(t.getString("bizKey"));
                }
                context.save(t.getString("bizKey"), t.getBoolean("fc"));
            } catch (Exception e) {
                log.error("availFreq exception:{}", ExceptionUtils.getFullStackTrace(e));
                return Result.failure("availFreq occurrence exception");
            }
        }

        return Result.success(context.dump());
    }

    Integer calcSeconds(Long cur, Long endTime) {
        long seconds = (endTime - cur) / 1000;
        return (int) seconds;
    }

    public Result<JSONObject> addFreq(FreqQuery freqQuery) {
        FreqContext context = new FreqContext("addFreq", freqQuery.getBizIds().get(0),
                Optional.ofNullable(freqQuery.getExtra())
                        .map(e -> e.get("useCache"))
                        .map(Object::toString)
                        .map(Boolean::valueOf)
                        .orElse(true));
        Locker locker = new Locker(this.jc, context.getRecorder(), freqQuery.getBizIds().get(0));
        locker.tryLock();

        try {
            Long cur = System.currentTimeMillis();
            List<User> users = fetchUsers(cur, freqQuery.getBizIds().get(0), freqQuery.getBizIds(), context);
            FreqInfoBuilder freqInfoBuilder = new FreqInfoBuilder(cur);
            for (String bizKey : freqQuery.getBizKeys()) {
                List<FreqBO> freqBoList = this.store.getFreqBOS(context, bizKey);
                context.getRecorder().setBizCode(bizKey);
                if (CollectionUtils.isEmpty(freqBoList)) {
                    context.saveEmptyConfigKey(bizKey);
                    continue;
                }

                boolean fc = false;
                for (FreqBO freqBO : freqBoList) {
                    for (User user : users) {
                        User.AvailResult availResult = user.avail(freqBO, "freq");
                        if (!availResult.isAvail()) {
                            context.getRecorder().setBizIds(Lists.newArrayList(user.getId())).setStep("freqOverrun")
                                    .setResult(availResult.getKey() + ":" + availResult.getCnt())
                                    .record();
                            fc = true;
                            break;
                        }
                    }
                    if (fc) {
                        break;
                    }
                }

                if (!fc) {
                    for (FreqBO freqBO : freqBoList) {
                        List<ItemBO> itemBoList = freqBO.getItemBOList();
                        String rateKey = freqBO.getRateBO().getKey();
                        for (ItemBO itemBO : itemBoList) {
                            if ("stock".equals(itemBO.getType())) {
                                JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, freqQuery.getBizIds().get(0), rateKey);

                                int ret = ((Long) jc.eval(LuaScript.INCRBY,
                                        Collections.singletonList(freqInfo.getString("key")),
                                        Arrays.asList(freqBO.getRateBO().getCnt().toString(),
                                                itemBO.getAmount().toString(),
                                                calcSeconds(cur, freqInfo.getLong("endTime")).toString()))).intValue();
                                if (ret == 0) {
                                    fc = true;
                                    context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("stockOverrun").setItemBO(itemBO).record();
                                    context.save(bizKey, fc);
                                    throw new StockLimitException("stock limit, freqDTO:" + freqQuery);
                                } else {
                                    context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("stockIncr").setItemBO(itemBO).record();
                                }
                            }
                        }
                    }
                }

                if (!fc) {
                    for (FreqBO freqBO : freqBoList) {
                        for (User user : users) {
                            user.add(freqBO);
                        }
                    }
                }

                context.save(bizKey, fc);
            }

            for (User user : users) {
                jc.set(freqPrefix + user.getId(), user.dump());
            }
        } catch (StockLimitException e) {
            log.error("stock limit, freqDTO:{}", freqQuery);
        } catch (Exception e) {
            log.error("addFreq error ", e);
            context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("except").setResult(e.getMessage()).record();
            return Result.failure("freq add occurrence exception");
        } finally {
            locker.releaseLock();
        }
        return Result.success(context.dump());
    }

    public Result<JSONObject> reduceFreq(FreqQuery freqQuery) {
        FreqContext context = new FreqContext("reduceFreq", freqQuery.getBizIds().get(0),
                Optional.ofNullable(freqQuery.getExtra())
                        .map(e -> e.get("useCache"))
                        .map(Object::toString)
                        .map(Boolean::valueOf)
                        .orElse(true));

        Locker locker = new Locker(this.jc, context.getRecorder(), freqQuery.getBizIds().get(0));
        locker.tryLock();

        try {
            Long cur = System.currentTimeMillis();
            List<User> users = fetchUsers(cur, freqQuery.getBizIds().get(0), freqQuery.getBizIds(), context);

            for (String bizKey : freqQuery.getBizKeys()) {
                context.getRecorder().setBizCode(bizKey);
                List<FreqBO> freqBoList = this.store.getFreqBOS(context, bizKey);
                if (CollectionUtils.isEmpty(freqBoList)) {
                    context.saveEmptyConfigKey(bizKey);
                    continue;
                }

                //库存恢复
                if ("ON".equals("")) {
                    boolean ignoreReverseStock = Optional.ofNullable(freqQuery.getIgnoreReverseStock()).orElse(false);
                    for (FreqBO freqBO : freqBoList) {
                        List<ItemBO> itemBoList = freqBO.getItemBOList();
                        String rateKey = freqBO.getRateBO().getKey();
                        for (ItemBO itemBO : itemBoList) {
                            if ("stock".equals(itemBO.getType()) && !ignoreReverseStock) {
                                FreqInfoBuilder freqInfoBuilder = new FreqInfoBuilder(cur);
                                JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, freqQuery.getBizIds().get(0), rateKey);
                                int ret = ((Long) jc.eval(LuaScript.DECREASE,
                                        Collections.singletonList(freqInfo.getString("key")),
                                        Arrays.asList(freqBO.getRateBO().getCnt().toString(),
                                                itemBO.getAmount().toString(),
                                                calcSeconds(cur, freqInfo.getLong("endTime")).toString()))).intValue();
                                if (ret == -1) {
                                    context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("stockNegative").setItemBO(itemBO).record();
                                    break;
                                } else if (ret == 0) {
                                    context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("stockZero").setItemBO(itemBO).record();
                                    break;
                                } else {
                                    context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("stockDecr").setItemBO(itemBO).record();
                                }
                            }
                        }
                    }
                }

                for (FreqBO freqBO : freqBoList) {
                    for (User user : users) {
                        user.release(freqBO);
                    }
                }
            }

            for (User user : users) {
                jc.set(freqPrefix + user.getId(), user.dump());
            }
        } catch (Exception e) {
            log.error("reduceFreq error ", e);
            context.getRecorder().setBizIds(freqQuery.getBizIds()).setStep("except").setResult(e.getMessage()).record();
            return Result.failure("freq add occurrence exception");
        } finally {
            locker.releaseLock();
        }

        return Result.success(context.dump());
    }
}
