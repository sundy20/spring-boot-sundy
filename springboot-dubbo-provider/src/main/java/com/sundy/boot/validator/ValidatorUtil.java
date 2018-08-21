package com.sundy.boot.validator;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Function: hibernate 校验工具
 *
 * @author wangzeng
 * @version 1.0
 * @since JDK 1.8
 */
public class ValidatorUtil {

    private static final Configuration<?> config;

    private static final Validator validator;

    static {

        config = Validation.byDefaultProvider().configure();

        validator = config.buildValidatorFactory().getValidator();

    }

    public static <T> Map<String, String> objectCheck(T object, Class<?>... groups) {

        Map<String, String> resultMap = new LinkedHashMap<>();

        Set<ConstraintViolation<T>> set = validator.validate(object, groups);

        for (ConstraintViolation<T> cv : set) {

            resultMap.put(cv.getPropertyPath().toString(), cv.getMessage());

        }

        return resultMap;

    }

}
