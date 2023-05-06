package com.sundy.share.service;

import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.*;

import java.util.List;

/**
 * @author zeng.wang
 * @description 服务流
 */
public interface FlowService {

    /**
     * 正向
     */
    Result<ForwardDTO> forward(ForwardQuery forwardQuery);

    /**
     * 逆向
     */
    Result<ForwardDTO> reverse(ReverseQuery reverseQuery);

    /**
     * 是否可逆向
     */
    Result<List<ReverseAllowDTO>> checkReverse(ReverseAllowQuery reverseAllowQuery);
}
