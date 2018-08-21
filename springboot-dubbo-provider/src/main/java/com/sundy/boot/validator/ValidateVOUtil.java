package com.sundy.boot.validator;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ValidateVOUtil {

    private static final Logger log = LoggerFactory.getLogger(ValidateVOUtil.class);

    public static <T> Map<String, String> checkVO(T obj, Class<?>... groups) {

        if (null == obj) {

            log.info("前端所传的参数为null");

            return new HashMap<String, String>() {

                {
                    put("voInstance", "vo对象不能为null");
                }
            };
        }

        Map<String, String> validateResMap = ValidatorUtil.objectCheck(obj, groups);

        if (!CollectionUtils.isEmpty(validateResMap)) {

            log.info("业务参数校验失败,校验结果：{}", JSON.toJSONString(validateResMap));

            return validateResMap;
        }

        return Collections.emptyMap();
    }
}
