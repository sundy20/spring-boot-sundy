package com.sundy.boot.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sundy.boot.exception.BizException;
import com.sundy.boot.inventory.repository.FreqRepository;
import com.sundy.boot.inventory.util.FreqRecorder;
import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.FreqQuery;
import com.sundy.share.service.FreqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service(version = "1.0")
public class FreqServiceImpl implements FreqService {

    @Autowired
    private FreqRepository freqRepository;

    private void check(List<String> bizIds, List<String> bizKeys) {
        if (CollectionUtils.isEmpty(bizIds)) {
            throw new BizException("bizIds is empty");
        }
        if (CollectionUtils.isEmpty(bizKeys)) {
            throw new BizException("bizkeys is empty");
        }
    }

    @Override
    public Result<JSONObject> availFreq(FreqQuery query) {
        try {
            check(query.getBizIds(), query.getBizKeys());
            return freqRepository.availFreq(query);
        } catch (BizException e) {
            log.error("[FreqService.availFreqException] freqQuery={} error ", JSON.toJSONString(query), e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            FreqRecorder recorder = new FreqRecorder();
            recorder.setAction("availFreq").setBizIds(query.getBizIds().toString()).setStep("except").setResult(e.getMessage()).record();
            log.error("[FreqService.availFreqException] freqQuery={} error ", JSON.toJSONString(query), e);
            return Result.failure(e.getMessage());
        }
    }

    @Override
    public Result<JSONObject> addFreq(FreqQuery freqQuery) {
        try {
            check(freqQuery.getBizIds(), freqQuery.getBizKeys());
            Result<JSONObject> result = freqRepository.addFreq(freqQuery);
            log.info("[FreqService.addFreq] freqDTO={} return={}", JSON.toJSONString(freqQuery), JSON.toJSONString(result));
            return result;
        } catch (BizException e) {
            log.error("[FreqService.addFreqException] freqDTO={} error ", JSON.toJSONString(freqQuery), e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            FreqRecorder recorder = new FreqRecorder();
            recorder.setAction("addFreq").setBizIds(freqQuery.getBizIds().toString()).setStep("except").setResult(e.getMessage()).record();
            log.error("[FreqService.addFreqException] freqDTO={} error ", JSON.toJSONString(freqQuery), e);
            return Result.failure(e.getMessage());
        }
    }

    @Override
    public Result<JSONObject> reduceFreq(FreqQuery freqQuery) {
        try {
            check(freqQuery.getBizIds(), freqQuery.getBizKeys());
            Result<JSONObject> result = freqRepository.reduceFreq(freqQuery);
            log.info("[FreqService.reduceFreq] freqDTO={} return={}", JSON.toJSONString(freqQuery), JSON.toJSONString(result));
            return result;
        } catch (BizException e) {
            log.error("[FreqService.reduceFreqException] freqDTO={} error ", JSON.toJSONString(freqQuery), e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            FreqRecorder recorder = new FreqRecorder();
            recorder.setAction("reduceFreq").setBizIds(freqQuery.getBizIds().toString()).setStep("except").setResult(e.getMessage()).record();
            log.error("[FreqService.reduceFreqException] freqDTO={} error ", JSON.toJSONString(freqQuery), e);
            return Result.failure(e.getMessage());
        }
    }
}
