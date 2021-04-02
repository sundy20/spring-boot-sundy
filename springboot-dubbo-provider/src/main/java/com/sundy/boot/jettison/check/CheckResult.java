package com.sundy.boot.jettison.check;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zeng.wang
 * @description 校验结果
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CheckResult {
    private boolean checkPass;
    private String code;
    private String message;

    public static CheckResult success() {
        return CheckResult.builder()
                .checkPass(true)
                .build();
    }
}
