package com.sundy.share.flowApi;

/**
 * @author zeng.wang
 * @description 逆向请求参数
 */
public class ReverseQuery extends BaseQuery {

    /**
     * 用户信息
     */
    private UserParam userParam;

    /**
     * 逆向key
     */
    private String reverseKey;

    /**
     * 请求id
     */
    private String requestId;

    public UserParam getUserParam() {
        return userParam;
    }

    public void setUserParam(UserParam userParam) {
        this.userParam = userParam;
    }

    public String getReverseKey() {
        return reverseKey;
    }

    public void setReverseKey(String reverseKey) {
        this.reverseKey = reverseKey;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
