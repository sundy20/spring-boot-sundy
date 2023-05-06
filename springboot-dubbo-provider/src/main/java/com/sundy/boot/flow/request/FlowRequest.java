package com.sundy.boot.flow.request;

import com.sundy.share.flowApi.UserParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 服务流请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowRequest implements Serializable {

    private static final long serialVersionUID = 4176256184345731870L;

    //流程code
    private String flowCode;

    //流程key
    private String processDefKey;

    //流程版本号
    private Integer version;

    //流程成功结果key
    private String processSuccessKey;

    //权益类型 业务code
    private String bizCode;

    //业务id
    private String bizId;

    //源业务id
    private String originBizId;

    //序列id
    private Long seqId;

    private String requestId;

    //用户参数
    private UserParam userParam;

    //库存频次
    private List<String> freqKey;

    //库存频次id
    private List<String> freqId;

    //业务请求参数
    private Map<String, Object> bizParams;

    //业务动作标识
    private String code;

    //业务类型
    private String bizType;

    //流程节点透传数据
    private Map<String, Object> flowNodeData;

    /**
     * 启用用户颗粒度锁 默认true：启用  false：不启用
     */
    private Boolean userLockFlag = true;

    //自定义细颗粒度锁bizId
    private String lockBizId;
}
