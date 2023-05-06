package com.sundy.share.flowApi;

/**
 * @author zeng.wang
 * @description 是否允许逆向的模型
 */
public class ReverseAllowDTO extends BaseDTO {

    /**
     * 逆向key
     */
    private String reverseKey;

    /**
     * 是否可逆向 true：可逆向 false：不可逆向
     */
    private boolean canReverse;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 兑换明细
     */
    private DetailDTO detailDTO;

    public String getReverseKey() {
        return reverseKey;
    }

    public void setReverseKey(String reverseKey) {
        this.reverseKey = reverseKey;
    }

    public boolean isCanReverse() {
        return canReverse;
    }

    public void setCanReverse(boolean canReverse) {
        this.canReverse = canReverse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DetailDTO getDetailDTO() {
        return detailDTO;
    }

    public void setDetailDTO(DetailDTO detailDTO) {
        this.detailDTO = detailDTO;
    }
}
