/**
 * Copyright (c) 2005-2016, cnfuelteam (fuelteam@163.com)"
 * <p>
 * Licensed under The MIT License (MIT)
 */
package com.sundy.boot.dao;

import com.sundy.boot.domain.FuturesSubscribe;

import java.util.List;
import java.util.Map;

/**
 * 描述： 操作 （futures_subscribe）
 * <p>
 * 作者： plus
 * 时间： 2017-11-28 16:40:34
 */
public interface FuturesSubscribeDao {

    public FuturesSubscribe findById(Long id);

    public List<FuturesSubscribe> findByIds(List<Long> ids);

    public List<FuturesSubscribe> findByExample(FuturesSubscribe futuresSubscribe);

    public int count(FuturesSubscribe futuresSubscribe);

    public List<FuturesSubscribe> findByParams(Map<String, Object> params);

    public int countByParams(Map<String, Object> params);

    public List<FuturesSubscribe> findAll();

    public int save(FuturesSubscribe futuresSubscribe);

    public int update(FuturesSubscribe futuresSubscribe);

    public int updateByUid(FuturesSubscribe futuresSubscribe);

    public int delete(Long id);

    public int saveOrUpdate(FuturesSubscribe futuresSubscribe);

    public int batchSave(List<FuturesSubscribe> futuresSubscribeList);

    public int batchUpdate(List<FuturesSubscribe> futuresSubscribeList);

    public int batchUpdateByUid(List<FuturesSubscribe> futuresSubscribeList);

    public int batchDelete(List<Long> ids);

    public int batchDeleteByUserId(FuturesSubscribe futuresSubscribe);
}