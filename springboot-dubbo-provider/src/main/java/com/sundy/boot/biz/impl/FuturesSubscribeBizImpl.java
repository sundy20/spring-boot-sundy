/**
 * Copyright (c) 2005-2016, cnfuelteam (fuelteam@163.com)"
 * <p>
 * Licensed under The MIT License (MIT)
 */
package com.sundy.boot.biz.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sundy.boot.dao.FuturesSubscribeDao;
import com.sundy.boot.domain.FuturesSubscribe;
import com.sundy.boot.biz.FuturesSubscribeBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述： （futures_subscribe） 接口实现
 * <p>
 * 作者： plus
 * 时间： 2017-11-28 16:40:34
 */
@Service
public class FuturesSubscribeBizImpl implements FuturesSubscribeBiz {

    @Autowired
    private FuturesSubscribeDao futuresSubscribeDao;

    @Override
    public PageInfo<FuturesSubscribe> findPage(int pageNum, int pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNum, pageSize);
        List<FuturesSubscribe> list = futuresSubscribeDao.findByParams(params);
        return new PageInfo<>(list);
    }

    @Override
    public List<FuturesSubscribe> findAll() {
        Map<String, Object> params = new HashMap<String, Object>();
        return futuresSubscribeDao.findByParams(params);
    }

    @Override
    public List<FuturesSubscribe> findByUserId(Long userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("deleted", "N");
        return futuresSubscribeDao.findByParams(params);
    }

    @Override
    @Transactional
    public int batchUpdateByUserId(Long userId, List<FuturesSubscribe> futuresSubscribeList) {

        FuturesSubscribe futuresSubscribe = new FuturesSubscribe();

        futuresSubscribe.setDeleted("Y");

        futuresSubscribe.setUserId(userId);

        futuresSubscribe.setUpdateAt(new Date());

        int c = futuresSubscribeDao.batchDeleteByUserId(futuresSubscribe);

        if (!CollectionUtils.isEmpty(futuresSubscribeList)) {

            c = futuresSubscribeDao.batchSave(futuresSubscribeList);
        }

        return c;
    }

    @Override
    public int countByUserId(Long userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", userId);
        params.put("deleted", "N");
        return futuresSubscribeDao.countByParams(params);
    }

    @Override
    public List<FuturesSubscribe> findByExample(FuturesSubscribe futuresSubscribe) {
        return futuresSubscribeDao.findByExample(futuresSubscribe);
    }

    @Override
    public int batchSave(List<FuturesSubscribe> futuresSubscribeList, Long userId) {

        return futuresSubscribeDao.batchSave(futuresSubscribeList);
    }

    @Override
    public int batchDelete(List<Long> ids, Long userId) {

        final int[] count = {0};

        ids.forEach(id -> {

            FuturesSubscribe futuresSubscribe = new FuturesSubscribe();

            futuresSubscribe.setContractId(id);

            futuresSubscribe.setUserId(userId);

            futuresSubscribe.setDeleted("Y");

            futuresSubscribe.setUpdateAt(new Date());

            count[0] += futuresSubscribeDao.updateByUid(futuresSubscribe);
        });

        return count[0];
    }

    @Override
    public int batchUpdate(List<FuturesSubscribe> futuresSubscribeList) {
        return futuresSubscribeDao.batchUpdate(futuresSubscribeList);
    }

    @Override
    public FuturesSubscribe get(Long id) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        List<FuturesSubscribe> list = futuresSubscribeDao.findByParams(params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public int save(FuturesSubscribe futuresSubscribe) {
        futuresSubscribe.setCreateAt(new Date());
        return futuresSubscribeDao.save(futuresSubscribe);
    }

    @Override
    public int update(FuturesSubscribe futuresSubscribe) {
        futuresSubscribe.setUpdateAt(new Date());
        return futuresSubscribeDao.update(futuresSubscribe);
    }

}

