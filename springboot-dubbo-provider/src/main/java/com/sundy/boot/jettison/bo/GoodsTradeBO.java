package com.sundy.boot.jettison.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zeng.wang
 * @description 货品交易结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsTradeBO extends BaseBO {
    private static final long serialVersionUID = 6540092474865052770L;
    private String message;
}
