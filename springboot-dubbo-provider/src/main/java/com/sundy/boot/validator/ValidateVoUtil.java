package com.sundy.boot.validator;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * vo验证
 *
 * @author wangzeng
 * @version 1.0
 */
@Slf4j
public class ValidateVoUtil {

    public static <T> Map<String, String> validateVO(T obj, Class<?>... groups) {
        if (null == obj) {
            log.info("前端所传的参数为null");
            return new HashMap<String, String>(1) {
                {
                    put("voInstance", "vo对象不能为null");
                }
            };
        }
        Map<String, String> validateResMap = ValidateUtil.validateBean(obj, groups);
        if (!CollectionUtils.isEmpty(validateResMap)) {
            log.info("业务参数校验失败,校验结果：{}", JSON.toJSONString(validateResMap));
            return validateResMap;
        }
        return Collections.emptyMap();
    }
}
