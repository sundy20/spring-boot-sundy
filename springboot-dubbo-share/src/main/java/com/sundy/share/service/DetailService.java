package com.sundy.share.service;

import com.sundy.share.dto.Result;
import com.sundy.share.flowApi.DetailDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface DetailService {

    /**
     * 根据userId和outKey列表查询正向对应明细列表(业务成功状态下的)
     */
    Result<List<DetailDTO>> listByUserIdAndOutKeys(Long userId, List<String> outKeyList);

    /**
     * 根据userId和开始时间查询正向兑换明细列表
     */
    Result<List<DetailDTO>> listByParams(Long userId, LocalDateTime beginTime);

    /**
     * 查询指定条件的明细列表(业务成功状态下的)
     */
    Result<List<DetailDTO>> listByUserIdAndBizCode(Long userId, String bizCode, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询指定条件的明细列表(业务成功状态下的)
     */
    Result<List<DetailDTO>> listByUserIdAndBizCodes(Long userId, List<String> bizCodes, LocalDateTime beginTime, LocalDateTime endTime);
}
