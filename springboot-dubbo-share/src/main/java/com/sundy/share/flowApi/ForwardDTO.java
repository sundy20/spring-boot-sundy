package com.sundy.share.flowApi;

/**
 * @author zeng.wang
 * @description 服务流交互动作结果
 */
public class ForwardDTO extends BaseDTO {

    /**
     * true：成功 false：失败
     */
    private boolean success;

    /**
     * 业务失败code
     */
    private String errorCode;

    /**
     * 交互结果的提示信息
     */
    private String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
