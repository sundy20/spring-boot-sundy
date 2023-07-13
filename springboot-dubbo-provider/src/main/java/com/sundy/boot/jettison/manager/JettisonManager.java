package com.sundy.boot.jettison.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sundy.boot.freq.FreqManager;
import com.sundy.boot.jettison.bo.*;
import com.sundy.boot.jettison.config.GoodsConfigDTO;
import com.sundy.boot.jettison.config.ResConfigDTO;
import com.sundy.boot.jettison.config.ShelfConfigDTO;
import com.sundy.boot.jettison.constant.GoodsConstant;
import com.sundy.boot.jettison.constant.TradeMessageUtil;
import com.sundy.boot.jettison.dto.DataItemDTO;
import com.sundy.boot.jettison.dto.GoodsTradeDTO;
import com.sundy.boot.jettison.dto.ResGoodsDTO;
import com.sundy.boot.jettison.enums.ResTypeEnum;
import com.sundy.boot.jettison.goods.GoodsHandlerFactory;
import com.sundy.boot.jettison.query.GoodsTradeParam;
import com.sundy.boot.jettison.query.GoodsTradeQuery;
import com.sundy.boot.jettison.query.ResParam;
import com.sundy.boot.jettison.query.ResQuery;
import com.sundy.boot.jettison.recall.RecallHandlerFactory;
import com.sundy.boot.utils.AsyncUtil;
import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.FreqQuery;
import com.sundy.share.flowApi.UserParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zeng.wang
 * @description 投放管理
 */
@Slf4j
@Component
public class JettisonManager {

    @Autowired
    private ResGoodsConfigManager resGoodsConfigManager;

    @Qualifier("resGoodsExecutor")
    @Autowired
    private ThreadPoolTaskExecutor resGoodsExecutor;

    @Qualifier("goodsDataExecutor")
    @Autowired
    private ThreadPoolTaskExecutor goodsDataExecutor;

    @Autowired
    private GoodsHandlerFactory goodsHandlerFactory;

    @Autowired
    private RecallHandlerFactory recallHandlerFactory;

    @Autowired
    private TargetCrowdManager targetCrowdManager;

    @Autowired
    private FreqManager freqManager;

    /**
     * 货品查询
     */
    public List<ResGoodsDTO> queryResGoods(ResQuery resQuery) {
        //获取资源位code对应的货品配置列表
        List<Pair<String, List<ResConfigDTO>>> pairs = Lists.newArrayList();
        List<ResParam> resParamList = resQuery.getResParams();
        resParamList.forEach(resParam -> {
            String resCode = resParam.getResCode();
            List<ResConfigDTO> resConfigs = getResConfigsByCode(resCode);
            Pair<String, List<ResConfigDTO>> pair = Pair.of(resCode, resConfigs);
            pairs.add(pair);
        });

        List<Pair<String, List<Future<Optional<ResGoodsDTO>>>>> pairList = Lists.newArrayList();
        pairs.forEach(pair -> {
            List<ResConfigDTO> resConfigs = pair.getSecond();
            List<Future<Optional<ResGoodsDTO>>> futureList = Lists.newArrayList();
            resConfigs.forEach(resConfigDTO -> {
                //遍历货品配置的货架列表，异步判断每个货架的人群定向 资源位货品数据量判空
                Future<Optional<ResGoodsDTO>> optionalFuture = AsyncUtil.asyncCall(resGoodsExecutor, () -> matchedGoods(resConfigDTO, resQuery));
                futureList.add(optionalFuture);
            });
            Pair<String, List<Future<Optional<ResGoodsDTO>>>> stringListPair = Pair.of(pair.getFirst(), futureList);
            pairList.add(stringListPair);
        });

        List<ResGoodsDTO> resGoodsDtos = Lists.newArrayList();
        pairList.forEach(stringListPair -> {
            String resCode = stringListPair.getFirst();
            ResGoodsDTO resGoodsDTO = ResGoodsDTO.builder().resCode(resCode).build();
            List<Future<Optional<ResGoodsDTO>>> futureList = stringListPair.getSecond();
            if (futureList.size() == 1) {//资源code
                Optional<ResGoodsDTO> optional = AsyncUtil.futureGet(futureList.get(0), GoodsConstant.ASYNC_TIMEOUT, TimeUnit.MILLISECONDS, Optional.empty());
                if (Objects.nonNull(optional)) {
                    optional.ifPresent(resGoodsDtos::add);
                }
            } else if (futureList.size() == 0) {
                log.warn("resQuery:{} resGoodsFutureList size is 0", JSON.toJSONString(resQuery));
            } else {//聚合code
                resGoodsDTO.setResType(ResTypeEnum.AGGR.getType());
                final int[] i = {1};
                List<JSONObject> jsonObjects = Lists.newArrayList();
                Map<String, Object> attribute = Maps.newHashMap();
                futureList.forEach(optionalFuture -> {
                    Optional<ResGoodsDTO> optional = AsyncUtil.futureGet(optionalFuture, GoodsConstant.ASYNC_TIMEOUT, TimeUnit.MILLISECONDS, Optional.empty());
                    if (Objects.nonNull(optional)) {
                        if (optional.isPresent()) {
                            ResGoodsDTO resGoodsDTO1 = optional.get();
                            List<DataItemDTO> data = resGoodsDTO1.getData();
                            if (!CollectionUtils.isEmpty(data)) {//res 判空
                                if (i[0] == 1) {
                                    resGoodsDTO.setData(data);
                                    Map<String, Object> goodsAttribute = resGoodsDTO1.getAttribute();
                                    if (!CollectionUtils.isEmpty(goodsAttribute)) {
                                        attribute.putAll(goodsAttribute);
                                    }
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(GoodsConstant.RES_CODE, resGoodsDTO1.getResCode());
                                jsonObject.put(GoodsConstant.RES_NAME, resGoodsDTO1.getResName());
                                jsonObject.put(GoodsConstant.RES_TYPE, resGoodsDTO1.getResType());
                                jsonObject.put(GoodsConstant.RES_DESC, resGoodsDTO1.getResDesc());
                                jsonObjects.add(jsonObject);
                                i[0]++;
                            }
                        }
                    }
                });
                attribute.put(GoodsConstant.RES_LIST, jsonObjects);
                resGoodsDTO.setAttribute(attribute);
                resGoodsDtos.add(resGoodsDTO);
            }
        });
        return resGoodsDtos;
    }

    /**
     * 获取资源位配置列表
     */
    private List<ResConfigDTO> getResConfigsByCode(String resCode) {
        //聚合类型需要返回子资源位
        Optional<Map<String, ResConfigDTO>> mapOptional = resGoodsConfigManager.getResConfigs();
        if (mapOptional.isPresent()) {
            Map<String, ResConfigDTO> resConfigMap = mapOptional.get();
            ResConfigDTO resConfigDTO = resConfigMap.get(resCode);
            if (Objects.nonNull(resConfigDTO)) {
                if (resConfigDTO.getResType().equals(ResTypeEnum.AGGR.getType())) {
                    //获取子资源位列表
                    List<ResConfigDTO> resConfigs = Lists.newArrayList();
                    List<JSONObject> jsonObjects = JSONArray.parseArray(resConfigDTO.getAttribute().get(GoodsConstant.RES_LIST).toString(), JSONObject.class);
                    jsonObjects.forEach(json -> {
                        String code = json.getString(GoodsConstant.RES_CODE);
                        resConfigs.add(resConfigMap.get(code));
                    });
                    log.info("resCode:{} getResConfigsByCode return resConfigs:{}", resCode, JSON.toJSONString(resConfigs));
                    return resConfigs;
                } else {
                    List<ResConfigDTO> resConfigs = Lists.newArrayList(resConfigDTO);
                    log.info("resCode:{} getResConfigsByCode return resConfigs:{}", resCode, JSON.toJSONString(resConfigs));
                    return resConfigs;
                }
            }
        }
        log.warn("resCode:{} getResConfigsByCode return emptyList", resCode);
        return Collections.emptyList();
    }

    /**
     * 获取用户匹配的货架以及对应的货品列表
     */
    private Optional<ResGoodsDTO> matchedGoods(ResConfigDTO resConfigDTO, ResQuery resQuery) {
        //人群定向
        List<ShelfConfigDTO> shelfs = targetCrowdManager.matchedTargetShelfs(resQuery.getUserParam(), resConfigDTO);
        log.info("resConfigDTO:{} trafficQuery:{} matchedShelf:{}", JSON.toJSONString(resConfigDTO), JSON.toJSONString(resQuery), JSON.toJSONString(shelfs));
        ResGoodsDTO resGoodsDTO = ResGoodsDTO.builder().build();
        resGoodsDTO.setResCode(resConfigDTO.getResCode());
        resGoodsDTO.setResType(resConfigDTO.getResType());
        resGoodsDTO.setResName(resConfigDTO.getResName());
        resGoodsDTO.setResDesc(resConfigDTO.getResDesc());
        if (!CollectionUtils.isEmpty(shelfs)) {
            List<Future<List<DataItemDTO>>> futureList = Lists.newArrayList();
            //获取每个货架的供给列表
            Map<String, List<GoodsConfigDTO>> goodsConfigMap = Maps.newHashMap();
            List<GoodsInner> goodsInners = Lists.newArrayList();
            shelfs.forEach(shelfDTO -> {
                List<GoodsInner> goodsInnerList = shelfDTO.getGoodsConfigs().stream().map(goodsConfigDTO -> {
                    GoodsInner goodsInner = new GoodsInner();
                    goodsInner.setPriority(shelfDTO.getPriority());
                    goodsInner.setGoodsCode(goodsConfigDTO.getGoodsCode());
                    return goodsInner;
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(goodsInnerList)) {
                    goodsInners.addAll(goodsInnerList);
                }

                Map<String, List<GoodsConfigDTO>> collect = shelfDTO.getGoodsConfigs().stream().collect(Collectors.groupingBy(GoodsConfigDTO::getGoodsType));
                if (!CollectionUtils.isEmpty(collect)) {
                    collect.forEach((goodsType, goodsConfigs) -> {
                        if (goodsConfigMap.containsKey(goodsType)) {
                            goodsConfigMap.get(goodsType).addAll(goodsConfigs);
                        } else {
                            goodsConfigMap.put(goodsType, goodsConfigs);
                        }
                    });
                }
            });
            goodsConfigMap.forEach((goodsType, goodsConfigs) -> {
                Future<List<DataItemDTO>> future = AsyncUtil.asyncCall(goodsDataExecutor, () -> getGoodsData(resQuery, resGoodsDTO, goodsConfigs, resConfigDTO.isPageable()));
                futureList.add(future);
            });
            List<DataItemDTO> data = Lists.newArrayList();
            futureList.forEach(listFuture -> {
                List<DataItemDTO> dataItems = AsyncUtil.futureGet(listFuture, GoodsConstant.ASYNC_TIMEOUT, TimeUnit.MILLISECONDS, Collections.emptyList());
                if (!CollectionUtils.isEmpty(dataItems)) {
                    data.addAll(dataItems);
                }
            });
            if (!CollectionUtils.isEmpty(data)) {
                List<DataItemDTO> dataItems = Lists.newArrayList();
                //货架货品排序
                List<GoodsInner> innerList = goodsInners.stream().sorted(Comparator.comparingLong(GoodsInner::getPriority).reversed()).collect(Collectors.toList());
                Map<String, List<DataItemDTO>> map = data.stream().collect(Collectors.groupingBy(DataItemDTO::getGoodsCode));
                innerList.forEach(goodsInner -> {
                    List<DataItemDTO> dtoList = map.get(goodsInner.getGoodsCode());
                    if (!CollectionUtils.isEmpty(dtoList)) {
                        dataItems.addAll(dtoList);
                    }
                });
                //资源位数据截断策略
                Map<String, Object> sortStrategy = resConfigDTO.getSortStrategy();
                if (!CollectionUtils.isEmpty(sortStrategy)) {
                    int count = Integer.parseInt(sortStrategy.get("topN").toString());
                    resGoodsDTO.setData(dataItems.subList(0, count));
                } else {
                    resGoodsDTO.setData(dataItems);
                }
                log.info("matchedGoods resConfigDTO:{} resQuery:{} resGoodsDTO:{}", JSON.toJSONString(resConfigDTO), JSON.toJSONString(resQuery), JSON.toJSONString(resGoodsDTO));
                return Optional.of(resGoodsDTO);
            }
        }
        return Optional.of(resGoodsDTO);
    }

    private List<DataItemDTO> getGoodsData(ResQuery resQuery, ResGoodsDTO resGoodsDTO, List<GoodsConfigDTO> goodsConfigs, boolean pageable) {
        GoodsConfigDTO goodsConfigDTO = goodsConfigs.get(0);
        Map<String, Object> recallStrategy = goodsConfigDTO.getRecallStrategy();
        String goodsType = goodsConfigDTO.getGoodsType();
        String recallStrategyCode = recallStrategy.get(GoodsConstant.RECALL_CODE).toString();
        UserParam userParam = resQuery.getUserParam();
        RecallParam recallParam = RecallParam.builder().recallCode(recallStrategyCode).build();
        recallParam.setExtra(recallStrategy);
        recallParam.setQueryExtra(resQuery.getExtra());
        boolean pageFlag = pageable && goodsConfigDTO.isPageable();
        if (pageFlag) {
            recallParam.setOffset(resQuery.getOffset());
            recallParam.setLimit(resQuery.getLimit());
        }
        recallParam.setUserParam(userParam);

        //TODO 召回前置处理 策略工厂模式

        RecallBO recallBO = recallHandlerFactory.getHandler(recallParam.getRecallCode()).handleRecall(recallParam);
        GoodsParam goodsParam = GoodsParam.builder().build();
        goodsParam.setGoodsType(goodsType);
        goodsParam.setUserParam(userParam);
        goodsParam.setRecallBO(recallBO);
        goodsParam.setGoodsConfigs(goodsConfigs);
        GoodsBO goodsBO = goodsHandlerFactory.getHandler(goodsType).handleQueryGoods(goodsParam);
        Map<String, Object> attribute = goodsBO.getAttribute();
        if (CollectionUtils.isEmpty(resGoodsDTO.getAttribute())) {
            resGoodsDTO.setAttribute(attribute);
        } else {
            if (!CollectionUtils.isEmpty(attribute)) {
                resGoodsDTO.getAttribute().putAll(attribute);
            }
        }
        if (goodsBO.isStatus()) {
            if (pageFlag) {
                Map<String, Object> stringObjectMap = Maps.newHashMap();
                RecallBO bo = goodsBO.getRecallBO();
                stringObjectMap.put(GoodsConstant.NEST_PAGE_ID, bo.getNextPageId());
                stringObjectMap.put(GoodsConstant.HAS_NEXT, bo.isHasNext());
                if (CollectionUtils.isEmpty(resGoodsDTO.getAttribute())) {
                    resGoodsDTO.setAttribute(stringObjectMap);
                } else {
                    resGoodsDTO.getAttribute().putAll(stringObjectMap);
                }
            }
            List<DataItemDTO> data = Lists.newArrayList();
            List<Map<String, Object>> goodsData = goodsBO.getGoodsData();
            goodsData.forEach(stringObjectMap -> {
                String goodsCode = stringObjectMap.get(GoodsConstant.GOODS_CODE).toString();
                DataItemDTO dataItemDTO = DataItemDTO.builder().build();
                dataItemDTO.setGoodsInst(goodsType + GoodsConstant.GOODS_INST_SPLIT + goodsCode + GoodsConstant.GOODS_INST_SPLIT + stringObjectMap.get(GoodsConstant.GOODS_KEY));
                dataItemDTO.setGoodsType(goodsType);
                dataItemDTO.setGoodsCode(goodsCode);
                //TODO 补全服务决定补全哪些字段 recallBo 补全策略工厂 fill

                dataItemDTO.setAttribute(stringObjectMap);
                data.add(dataItemDTO);
            });

            //TODO 召回补全后置处理 策略工厂模式

            return data;
        }
        return Collections.emptyList();
    }

    /**
     * 货品交易
     */
    public List<GoodsTradeDTO> tradeGoods(GoodsTradeQuery goodsTradeQuery) {
        UserParam userParam = goodsTradeQuery.getUserParam();
        List<GoodsTradeParam> goodsTradeParams = goodsTradeQuery.getGoodsTradeParams();
        Map<String, Object> extraMap = goodsTradeQuery.getExtra();
        if (!CollectionUtils.isEmpty(goodsTradeParams)) {
            List<GoodsTradeDTO> goodsTrades = Lists.newArrayList();
            goodsTradeParams.forEach(goodsTradeParam -> {
                String resCode = goodsTradeParam.getResCode();
                String goodsInst = goodsTradeParam.getGoodsInst();
                Map<String, Object> extra = goodsTradeParam.getExtra();
                GoodsTradeDTO goodsTradeDTO = GoodsTradeDTO.builder().resCode(resCode).tradeMessage(TradeMessageUtil.getTradeMessage()).build();
                List<ResConfigDTO> resConfigs = getResConfigsByCode(resCode);
                if (!CollectionUtils.isEmpty(resConfigs)) {
                    ResConfigDTO resConfigDTO = resConfigs.get(0);
                    GoodsConfigDTO goodsConfigDTO = null;
                    String goodsKey = null;
                    if (!StringUtils.isEmpty(goodsInst)) {
                        String[] strings = goodsInst.split(GoodsConstant.GOODS_INST_SPLIT_CHAR);
                        goodsTradeDTO.setGoodsInst(goodsInst);
                        if (strings.length == 3) {
                            String goodsCode = strings[1];
                            goodsKey = strings[2];
                            goodsConfigDTO = preTrade(userParam, resConfigDTO, goodsCode, goodsKey, goodsTradeDTO);
                        }
                    } else {
                        if (!CollectionUtils.isEmpty(extra)) {
                            goodsKey = extra.get(GoodsConstant.GOODS_KEY).toString();
                        }
                        goodsConfigDTO = preTrade(userParam, resConfigDTO, null, goodsKey, goodsTradeDTO);
                    }
                    if (Objects.nonNull(goodsConfigDTO)) {
                        Map<String, Object> map = Maps.newHashMap();
                        if (!CollectionUtils.isEmpty(extraMap)) {
                            map.putAll(extraMap);
                        }
                        if (!CollectionUtils.isEmpty(extra)) {
                            map.putAll(extra);
                        }
                        GoodsParam goodsParam = GoodsParam.builder().build();
                        goodsParam.setGoodsType(goodsConfigDTO.getGoodsType());
                        goodsParam.setGoodsKey(goodsKey);
                        goodsParam.setUserParam(userParam);
                        goodsParam.setExtra(map);
                        goodsParam.setGoodsConfigs(Lists.newArrayList(goodsConfigDTO));
                        //add频次
                        boolean addFreq = addFreq(userParam.getUserId(), resConfigDTO);
                        if (addFreq) {
                            GoodsTradeBO goodsTradeBO = goodsHandlerFactory.getHandler(goodsParam.getGoodsType()).handleTradeGoods(goodsParam);
                            if (goodsTradeBO.isStatus()) {
                                goodsTradeDTO.setTradeStatus(GoodsConstant.TRADE_SUCCESS_STATUS);
                                goodsTradeDTO.setAttribute(goodsTradeBO.getAttribute());
                                goodsTradeDTO.setTradeMessage(GoodsConstant.EMPTY_STR);
                            } else {
                                reduceFreq(userParam.getUserId(), resConfigDTO);
                                goodsTradeDTO.setTradeMessage(goodsTradeBO.getMessage());
                            }
                        }
                    }
                }
                goodsTrades.add(goodsTradeDTO);
            });
            return goodsTrades;
        }
        return Collections.emptyList();
    }

    /**
     * 人群定向check
     * 库存频次check
     */
    private GoodsConfigDTO preTrade(UserParam userParam, ResConfigDTO resConfigDTO, String goodsCode, String goodsKey, GoodsTradeDTO goodsTradeDTO) {
        if (StringUtils.isEmpty(goodsKey)) {
            return null;
        }

        //人群定向
        List<ShelfConfigDTO> shelfs = targetCrowdManager.matchedTargetShelfs(userParam, resConfigDTO);
        if (CollectionUtils.isEmpty(shelfs)) {
            return null;
        }

        //资源位频次check
        List<Map<String, Object>> mapList = resConfigDTO.getFreqKeys();
        if (!CollectionUtils.isEmpty(mapList)) {
            List<String> freqKeys = Lists.newArrayList();
            mapList.forEach(map -> freqKeys.add(map.get(GoodsConstant.FREQ_KEY).toString()));
            FreqQuery freqQuery = new FreqQuery();
            freqQuery.setBizKeys(freqKeys);
            freqQuery.setBizIds(Lists.newArrayList(userParam.getUserId().toString()));
            Result<JSONObject> checkFreq = freqManager.availFreq(freqQuery);
            Boolean ok = JSONObject.parseObject(checkFreq.getData().getString("OK"), new TypeReference<Boolean>() {
            });
            if (!ok) {
                goodsTradeDTO.setTradeMessage(mapList.get(0).get(GoodsConstant.FREQ_TEXT).toString());
                return null;
            }
        }

        GoodsConfigDTO goodsConfigDTO = new GoodsConfigDTO();
        if (StringUtils.isEmpty(goodsCode)) {
            //默认取top1货品配置
            ShelfConfigDTO shelfDTO = shelfs.stream().findFirst().get();
            List<GoodsConfigDTO> goodsConfigs = shelfDTO.getGoodsConfigs();
            if (CollectionUtils.isEmpty(goodsConfigs)) {
                return null;
            } else {
                goodsConfigDTO = goodsConfigs.stream().findFirst().get();
            }
        } else {
            boolean flag = false;
            for (ShelfConfigDTO shelfDTO : shelfs) {
                Optional<GoodsConfigDTO> optional = shelfDTO.getGoodsConfigs().stream().filter(configDTO -> configDTO.getGoodsCode().equals(goodsCode)).findAny();
                if (optional.isPresent()) {
                    flag = true;
                    goodsConfigDTO = optional.get();
                }
                if (flag) {
                    break;
                }
            }
            if (!flag) {
                return null;
            }
        }
        //货品库存频次
//        Map<String, Object> freq = goodsConfigDTO.getFreq();
//        if (CollectionUtils.isEmpty(freq)) {
//            return goodsConfigDTO;
//        }
        return goodsConfigDTO;
    }

    private boolean addFreq(Long userId, ResConfigDTO resConfigDTO) {
        List<Map<String, Object>> mapList = resConfigDTO.getFreqKeys();
        if (!CollectionUtils.isEmpty(mapList)) {
            List<String> freqKeys = Lists.newArrayList();
            FreqQuery freqQuery = new FreqQuery();
            freqQuery.setBizKeys(freqKeys);
            freqQuery.setBizIds(Lists.newArrayList(userId.toString()));
            mapList.forEach(map -> freqKeys.add(map.get(GoodsConstant.FREQ_KEY).toString()));
            Result<JSONObject> addFreq = freqManager.addFreq(freqQuery);
            return JSONObject.parseObject(addFreq.getData().getString("OK"), new TypeReference<Boolean>() {
            });
        } else {
            return Boolean.TRUE;
        }
    }

    private boolean reduceFreq(Long userId, ResConfigDTO resConfigDTO) {
        List<Map<String, Object>> mapList = resConfigDTO.getFreqKeys();
        if (!CollectionUtils.isEmpty(mapList)) {
            List<String> freqKeys = Lists.newArrayList();
            FreqQuery freqQuery = new FreqQuery();
            freqQuery.setBizKeys(freqKeys);
            freqQuery.setBizIds(Lists.newArrayList(userId.toString()));
            mapList.forEach(map -> freqKeys.add(map.get(GoodsConstant.FREQ_KEY).toString()));
            Result<JSONObject> reduceFreq = freqManager.reduceFreq(freqQuery);
            return JSONObject.parseObject(reduceFreq.getData().getString("OK"), new TypeReference<Boolean>() {
            });
        } else {
            return Boolean.TRUE;
        }
    }
}
