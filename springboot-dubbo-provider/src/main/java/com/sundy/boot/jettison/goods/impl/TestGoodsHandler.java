package com.sundy.boot.jettison.goods.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.sundy.boot.jettison.bo.GoodsBO;
import com.sundy.boot.jettison.bo.GoodsParam;
import com.sundy.boot.jettison.bo.GoodsTradeBO;
import com.sundy.boot.jettison.bo.RecallBO;
import com.sundy.boot.jettison.config.GoodsConfigDTO;
import com.sundy.boot.jettison.constant.GoodsConstant;
import com.sundy.boot.jettison.constant.TradeMessageUtil;
import com.sundy.boot.jettison.enums.GoodsTypeEnum;
import com.sundy.boot.jettison.goods.GoodsHandler;
import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.UserParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zeng.wang
 * @description 测试货品处理器
 */
@Slf4j
@Component
public class TestGoodsHandler implements GoodsHandler {

    /**
     * 获得货品类型
     */
    @Override
    public String getGoodsType() {
        return GoodsTypeEnum.TEST.getType();
    }

    /**
     * 货品查询处理
     */
    @Override
    public GoodsBO handleQueryGoods(GoodsParam goodsParam) {
        RecallBO recallBO = goodsParam.getRecallBO();
        GoodsBO goodsBO = GoodsBO.builder().build();
        if (recallBO.isStatus()) {
            try {
                UserParam userParam = goodsParam.getUserParam();
                List<Long> bizIds = JSONObject.parseObject(JSON.toJSONString(recallBO.getRecallIds()), new TypeReference<List<Long>>() {
                });
                Result<Map<Long, List<Object>>> mapResult = Result.success(Collections.emptyMap());
                if (mapResult.isSucceed()) {
                    List<Map<String, Object>> mapList = Lists.newArrayList();
                    Map<Long, List<Object>> data = mapResult.getData();
                    GoodsConfigDTO goodsConfigDTO = goodsParam.getGoodsConfigs().get(0);
                    List<Map<String, Object>> sku = goodsConfigDTO.getSkuList();
                    data.forEach((bizId, bizDataList) -> sku.forEach(objectMap -> {
                        Object object = bizDataList.get(Integer.parseInt(objectMap.get("index").toString()));
                        Map<String, Object> mapData = JSONObject.parseObject(JSON.toJSONString(object), new TypeReference<Map<String, Object>>() {
                        });
                        mapData.put(GoodsConstant.GOODS_KEY, bizId);
                        mapData.put(GoodsConstant.GOODS_CODE, goodsConfigDTO.getGoodsCode());
                        mapData.put(GoodsConstant.COST, objectMap.get(GoodsConstant.COST));
                        mapList.add(mapData);
                    }));
                    goodsBO.setStatus(true);
                    goodsBO.setRecallBO(recallBO);
                    goodsBO.setGoodsData(mapList);
                }
            } catch (Exception e) {
                log.error("TestGoodsHandler handleQueryGoods goodsParam:{} error: ", JSON.toJSONString(goodsParam), e);
            }
        }
        return goodsBO;
    }

    /**
     * 货品交易处理
     */
    @Override
    public GoodsTradeBO handleTradeGoods(GoodsParam goodsParam) {
        Map<String, Object> extra = goodsParam.getExtra();
        String goodsKey = goodsParam.getGoodsKey();
        GoodsTradeBO goodsTradeBO = new GoodsTradeBO();
        goodsTradeBO.setMessage(TradeMessageUtil.getTradeMessage());
        if (StringUtils.isEmpty(goodsKey) || CollectionUtils.isEmpty(extra)) {
            log.warn("goodsKey or extra is empty");
            return goodsTradeBO;
        }
        try {
            UserParam userParam = goodsParam.getUserParam();
            List<Map<String, Object>> sku = goodsParam.getGoodsConfigs().get(0).getSkuList();
            if (!CollectionUtils.isEmpty(sku)) {
                Map<String, Object> stringObjectMap = sku.stream().findFirst().get();
                String bizFlag = stringObjectMap.get("bizFlag").toString();
            }
            Long bizId = Long.parseLong(goodsKey);
            Result<Map<Long, List<Object>>> mapResult = Result.success(Collections.emptyMap());
            if (mapResult.isSucceed()) {
                Map<Long, List<Object>> data = mapResult.getData();
                if (!CollectionUtils.isEmpty(data)) {
                    List<Object> objectList = data.get(bizId);
                    if (!CollectionUtils.isEmpty(objectList)) {
                        if (Objects.nonNull(objectList.get(0))) {
                            //货品交易结果
                            Result<Object> tradeResult = Result.success(new Object());
                            Map<String, Object> mapData = JSONObject.parseObject(JSON.toJSONString(tradeResult), new TypeReference<Map<String, Object>>() {
                            });
                            goodsTradeBO.setAttribute(mapData);
                            if (tradeResult.isSucceed()) {
                                goodsTradeBO.setStatus(true);
                                goodsTradeBO.setMessage(GoodsConstant.EMPTY_STR);
                            } else {
                                goodsTradeBO.setMessage(tradeResult.getMsgInfo());
                            }
                        }
                    }
                }
            } else {
                goodsTradeBO.setMessage("empty data");
            }
        } catch (Exception e) {
            log.error("TestGoodsHandler handleTradeGoods goodsParam:{} error: ", JSON.toJSONString(goodsParam), e);
        }
        return goodsTradeBO;
    }
}
