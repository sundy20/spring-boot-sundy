package com.sundy.share.service;

import com.alibaba.fastjson.JSONObject;
import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.FreqQuery;


public interface FreqService {
    // 是否可用（至少还能增加一次）
    Result<JSONObject> availFreq(FreqQuery query);

    // 增加
    Result<JSONObject> addFreq(FreqQuery freqQuery);

    // 减少
    Result<JSONObject> reduceFreq(FreqQuery freqQuery);
}
