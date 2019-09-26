package com.sundy.boot.validator;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
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
    /**
     * 开启快速结束模式 failFast (true)
     */
    private static final Validator VALIDATOR =
            Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    public static <T> Map<String, String> objectCheck(T object, Class<?>... groups) {
        Map<String, String> resultMap = new HashMap<>();
        Set<ConstraintViolation<T>> constraintViolationSet = VALIDATOR.validate(object, groups);
        for (ConstraintViolation<T> constraintViolation : constraintViolationSet) {
            resultMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        return resultMap;
    }
}
