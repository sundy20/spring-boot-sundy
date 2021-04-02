/**
 * Copyright (c) 2005-2016, cnfuelteam (fuelteam@163.com)"
 * <p>
 * Licensed under The MIT License (MIT)
 */
package com.sundy.boot.bizManager;

import com.github.pagehelper.PageInfo;
import com.sundy.boot.domain.FuturesSubscribe;

import java.util.List;
import java.util.Map;

/**
 * 描述： （futures_subscribe） 接口
 * <p>
 * 作者： plus
 * 时间： 2017-11-28 16:40:34
 */
public interface FuturesSubscribeBiz {

    public PageInfo<FuturesSubscribe> findPage(int pageNum, int pageSize, Map<String, Object> params);

    public List<FuturesSubscribe> findAll();

    public FuturesSubscribe get(Long id);

    public int save(FuturesSubscribe futuresSubscribe);

    public int update(FuturesSubscribe futuresSubscribe);

    public List<FuturesSubscribe> findByUserId(Long userId);

    public int batchUpdateByUserId(Long userId, List<FuturesSubscribe> futuresSubscribeList);

    public int countByUserId(Long userId);

    public List<FuturesSubscribe> findByExample(FuturesSubscribe futuresSubscribe);

    public int batchSave(List<FuturesSubscribe> futuresSubscribeList, Long userId);

    public int batchDelete(List<Long> ids, Long userId);

    public int batchUpdate(List<FuturesSubscribe> futuresSubscribeList);

}