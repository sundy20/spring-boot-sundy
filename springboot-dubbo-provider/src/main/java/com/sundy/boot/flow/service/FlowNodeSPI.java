package com.sundy.boot.flow.service;


import com.sundy.boot.flow.request.FlowRequest;
import com.sundy.boot.flow.response.BizResult;

/**
 * @author zeng.wang
 * @description 流节点SPI
 */
public interface FlowNodeSPI<T> {

    /**
     * 发放
     *
     * @param request 流程输入参数
     */
    BizResult<T> produce(FlowRequest request);

    /**
     * 预消耗
     *
     * @param request 流程输入参数
     */
    BizResult<T> preConsume(FlowRequest request);

    /**
     * 确认消耗
     *
     * @param request 流程输入参数
     */
    BizResult<T> consume(FlowRequest request);

    /**
     * 消耗退回
     *
     * @param request 流程输入参数
     */
    BizResult<T> refund(FlowRequest request);
}
