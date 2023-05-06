package com.sundy.share.flowApi;

import java.util.List;

/**
 * @author zeng.wang
 * @description 是否可逆向请求参数
 */
public class ReverseAllowQuery extends BaseQuery {

    /**
     * 用户信息
     */
    private UserParam userParam;

    /**
     * 是否可逆向key列表
     */
    private List<String> reverseKeys;

    public UserParam getUserParam() {
        return userParam;
    }

    public void setUserParam(UserParam userParam) {
        this.userParam = userParam;
    }

    public List<String> getReverseKeys() {
        return reverseKeys;
    }

    public void setReverseKeys(List<String> reverseKeys) {
        this.reverseKeys = reverseKeys;
    }
}
