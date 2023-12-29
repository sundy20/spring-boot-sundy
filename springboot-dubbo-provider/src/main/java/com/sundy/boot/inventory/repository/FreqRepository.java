package com.sundy.boot.inventory.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sundy.boot.exception.BizException;
import com.sundy.boot.exception.StockLimitException;
import com.sundy.boot.inventory.dao.FreqDAO;
import com.sundy.boot.inventory.domain.Freq;
import com.sundy.boot.inventory.domain.Item;
import com.sundy.boot.inventory.util.*;
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

/**
 * 库存频次领域层
 */
@Component
@Slf4j
public class FreqRepository {

    @Autowired
    @Qualifier("inventoryRedis")
    private JedisCluster jedisCluster;

    @Autowired
    private FreqDAO freqDAO;

    @Qualifier("inventoryExecutor")
    @Autowired
    private ThreadPoolTaskExecutor executor;

    public JSONObject availFreqByKey(FreqQuery query, Long cur, List<Freq> freqBoList, String bizKey, FreqContext context) {
        FreqRecorder recorder = new FreqRecorder();
        recorder.setAction("availFreq").setBizIds(query.getBizIds().toString()).setBizCode(bizKey);

        JSONObject ret = new JSONObject();
        ret.put("bizKey", bizKey);
        ret.put("fc", false);
        boolean dump = Optional.ofNullable(query.getExtra()).map(e -> e.containsKey("dump")).orElse(false);
        try {
            FreqBuilder freqInfoBuilder = new FreqBuilder(cur);
            for (Freq freqBO : freqBoList) {
                List<Item> itemBoList = freqBO.getItemList();
                for (Item itemBO : itemBoList) {
                    if (!"stock".equals(itemBO.getType())) {
                        continue;
                    }

                    JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, query.getBizIds().get(0), freqBO.getRate().getKey());
                    String cnt = jedisCluster.get(freqInfo.getString("key"));

                    if (dump) {
                        JSONArray extra = Optional.ofNullable(ret.getJSONArray("extra")).orElseGet(JSONArray::new);
                        JSONObject t = new JSONObject();
                        t.put(freqInfo.getString("key"), cnt);
                        t.put("elapse", jedisCluster.ttl(freqInfo.getString("key")));
                        extra.add(t);
                        ret.put("extra", extra);
                    }
                    Long remain = Optional.ofNullable(cnt)
                            .map(Long::valueOf)
                            .map(e -> itemBO.getAmount() - e)
                            .orElse(itemBO.getAmount());
                    recorder.setStep(remain.toString()).setItemBO(itemBO).record();

                    if (cnt != null && Integer.parseInt(cnt) + freqBO.getRate().getCnt() > itemBO.getAmount()) {
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
                    String node = jedisCluster.get(Constants.FREQ_PREFIX + s);
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
            String node = jedisCluster.get(Constants.FREQ_PREFIX + bizId);
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
        Map<String, Future<List<Freq>>> map = Maps.newHashMap();
        for (String bizKey : query.getBizKeys()) {
            Future<List<Freq>> future = AsyncUtil.asyncCall(executor, () -> this.freqDAO.getFreqs(context, bizKey));
            map.put(bizKey, future);
        }

        for (Map.Entry<String, Future<List<Freq>>> entry : map.entrySet()) {
            String bizKey = entry.getKey();
            Future<List<Freq>> listFuture = entry.getValue();
            List<Freq> freqBoList = AsyncUtil.futureGet(listFuture, Constants.FREQ_ASYNC_MS, TimeUnit.MILLISECONDS, null);
            if (Objects.isNull(freqBoList)) {
                throw new BizException(String.format("bizKey：%s库存频次redis获取超时", bizKey));
            }
            if (CollectionUtils.isEmpty(freqBoList)) {
                context.saveEmptyConfigKey(bizKey);
                continue;
            }
            boolean haveStock = false;
            for (Freq freqBO : freqBoList) {
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
                List<Item> itemBoList = freqBO.getItemList();
                for (Item itemBO : itemBoList) {
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
        String bizId = freqQuery.getBizIds().get(0);
        FreqContext context = new FreqContext("addFreq", bizId,
                Optional.ofNullable(freqQuery.getExtra())
                        .map(e -> e.get("useCache"))
                        .map(Object::toString)
                        .map(Boolean::valueOf)
                        .orElse(true));
        FreqLocker locker = new FreqLocker(this.jedisCluster, context.getRecorder(),
                Optional.ofNullable(bizId).orElseGet(() -> bizId));
        locker.tryLock();

        try {
            Long cur = System.currentTimeMillis();
            List<User> users = fetchUsers(cur, bizId, freqQuery.getBizIds(), context);
            FreqBuilder freqInfoBuilder = new FreqBuilder(cur);
            for (String bizKey : freqQuery.getBizKeys()) {
                List<Freq> freqBoList = this.freqDAO.getFreqs(context, bizKey);
                context.getRecorder().setBizCode(bizKey);
                if (CollectionUtils.isEmpty(freqBoList)) {
                    context.saveEmptyConfigKey(bizKey);
                    continue;
                }

                boolean fc = false;
                for (Freq freqBO : freqBoList) {
                    for (User user : users) {
                        User.AvailResult availResult = user.avail(freqBO, "freq");
                        if (!availResult.isAvail()) {
                            context.getRecorder().setBizIds(user.getId()).setStep("freqOverrun")
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
                    for (Freq freqBO : freqBoList) {
                        List<Item> itemBoList = freqBO.getItemList();
                        String rateKey = freqBO.getRate().getKey();
                        for (Item itemBO : itemBoList) {
                            if ("stock".equals(itemBO.getType())) {
                                JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, bizId, rateKey);

                                int ret = ((Long) jedisCluster.eval(FreqLuaScript.INCRBY,
                                        Collections.singletonList(freqInfo.getString("key")),
                                        Arrays.asList(freqBO.getRate().getCnt().toString(),
                                                itemBO.getAmount().toString(),
                                                calcSeconds(cur, freqInfo.getLong("endTime")).toString()))).intValue();
                                if (ret == 0) {
                                    fc = true;
                                    context.getRecorder().setBizIds(bizId).setStep("stockOverrun").setItemBO(itemBO).record();
                                    context.save(bizKey, fc);
                                    throw new StockLimitException("stock limit, freqQuery:" + freqQuery);
                                } else {
                                    context.getRecorder().setBizIds(bizId).setStep("stockIncr").setItemBO(itemBO).record();
                                }
                            }
                        }
                    }
                }

                if (!fc) {
                    for (Freq freqBO : freqBoList) {
                        for (User user : users) {
                            user.add(freqBO);
                        }
                    }
                }

                context.save(bizKey, fc);
            }

            for (User user : users) {
                //TODO key ttl 设置
                jedisCluster.set(Constants.FREQ_PREFIX + user.getId(), user.dump());
            }
        } catch (StockLimitException e) {
            log.error("stock limit, freqQuery:{}", freqQuery);
        } catch (Exception e) {
            log.error("addFreq error ", e);
            context.getRecorder().setBizIds(bizId).setStep("except").setResult(e.getMessage()).record();
            return Result.failure("freq add occurrence exception");
        } finally {
            locker.releaseLock();
        }
        return Result.success(context.dump());
    }

    public Result<JSONObject> reduceFreq(FreqQuery freqQuery) {
        String bizId = freqQuery.getBizIds().get(0);
        FreqContext context = new FreqContext("reduceFreq", bizId,
                Optional.ofNullable(freqQuery.getExtra())
                        .map(e -> e.get("useCache"))
                        .map(Object::toString)
                        .map(Boolean::valueOf)
                        .orElse(true));

        FreqLocker locker = new FreqLocker(this.jedisCluster, context.getRecorder(),
                Optional.ofNullable(bizId).orElseGet(() -> bizId));
        locker.tryLock();

        try {
            Long cur = System.currentTimeMillis();
            List<User> users = fetchUsers(cur, bizId, freqQuery.getBizIds(), context);

            for (String bizKey : freqQuery.getBizKeys()) {
                context.getRecorder().setBizCode(bizKey);
                List<Freq> freqBoList = this.freqDAO.getFreqs(context, bizKey);
                if (CollectionUtils.isEmpty(freqBoList)) {
                    context.saveEmptyConfigKey(bizKey);
                    continue;
                }

                //库存恢复
                boolean ignoreReverseStock = Optional.ofNullable(freqQuery.getIgnoreReverseStock()).orElse(false);
                for (Freq freqBO : freqBoList) {
                    List<Item> itemBoList = freqBO.getItemList();
                    String rateKey = freqBO.getRate().getKey();
                    for (Item itemBO : itemBoList) {
                        if ("stock".equals(itemBO.getType()) && !ignoreReverseStock) {
                            FreqBuilder freqInfoBuilder = new FreqBuilder(cur);
                            JSONObject freqInfo = freqInfoBuilder.fetchFreqInfo(itemBO, bizId, rateKey);
                            int ret = ((Long) jedisCluster.eval(FreqLuaScript.DECREASE,
                                    Collections.singletonList(freqInfo.getString("key")),
                                    Arrays.asList(freqBO.getRate().getCnt().toString(),
                                            itemBO.getAmount().toString(),
                                            calcSeconds(cur, freqInfo.getLong("endTime")).toString()))).intValue();
                            if (ret == -1) {
                                context.getRecorder().setBizIds(bizId).setStep("stockNegative").setItemBO(itemBO).record();
                                break;
                            } else if (ret == 0) {
                                context.getRecorder().setBizIds(bizId).setStep("stockZero").setItemBO(itemBO).record();
                                break;
                            } else {
                                context.getRecorder().setBizIds(bizId).setStep("stockDecr").setItemBO(itemBO).record();
                            }
                        }
                    }
                }

                for (Freq freqBO : freqBoList) {
                    for (User user : users) {
                        user.release(freqBO);
                    }
                }
            }

            for (User user : users) {
                //TODO key ttl 设置
                jedisCluster.set(Constants.FREQ_PREFIX + user.getId(), user.dump());
            }
        } catch (Exception e) {
            log.error("reduceFreq error ", e);
            context.getRecorder().setBizIds(bizId).setStep("except").setResult(e.getMessage()).record();
            return Result.failure("freq add occurrence exception");
        } finally {
            locker.releaseLock();
        }

        return Result.success(context.dump());
    }
}
