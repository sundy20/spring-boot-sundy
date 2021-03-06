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
public class ValidateUtil {
    /**
     * 开启快速结束模式 failFast (true) 快速失败模式在校验过程中，当遇到第一个不满足条件的参数时就立即返回，不再继续后面参数的校验
     */
    private static final Validator VALIDATOR =
            Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();

    public static <T> Map<String, String> validateBean(T object, Class<?>... groups) {
        Map<String, String> resultMap = new HashMap<>();
        Set<ConstraintViolation<T>> constraintViolationSet = VALIDATOR.validate(object, groups);
        for (ConstraintViolation<T> constraintViolation : constraintViolationSet) {
            resultMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        return resultMap;
    }
}
