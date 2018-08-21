package com.sundy.boot.utils;

/**
 * @author plus.wang
 * @description array 非空判断
 * @date 2018/5/14
 */
public class ArrayUtil {

    /**
     * 判断数组是否为空
     */
    private static <T> boolean isEmptyArray(T[] array) {
        if (array == null || array.length == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断数组是否不为空
     */
    public static <T> boolean isNotEmptyArray(T[] array) {
        if (array != null && array.length > 0) {
            return true;
        } else {
            return false;
        }
    }
}
