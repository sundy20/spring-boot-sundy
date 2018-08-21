package com.sundy.boot.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author plus.wang
 * @description JAVA 正则表达式工具
 * @date 2018/5/16
 */
public class PatternUtil {

    /**
     * 判断一个字符串是否包含中文
     *
     * @param str
     */
    public static Boolean hasContainsChinese(String str) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        return isContains(pattern, str);
    }

    /**
     * 是否是数字
     *
     * @param str
     */
    public static Boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return isContains(pattern, str);
    }

    /**
     * 判断是否手机号码，适用于中国大陆
     *
     * @param str
     */
    public static Boolean isMoblie(String str) {
        Pattern pattern = Pattern.compile("^[1][0-9]{10}$");
        return isContains(pattern, str);
    }

    /**
     * 判断是否车牌号
     *
     * @param str
     */
    public static Boolean isPlate(String str) {
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5].+$");
        return isContains(pattern, str);
    }

    /**
     * 判断是否车牌号，适用于中国大陆
     *
     * @param str
     */
    public static Boolean isCNPlate(String str) {
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5][\\da-zA-Z]{6}$");
        return isContains(pattern, str);
    }

    /**
     * 判断一个字符串中是否包含有这些特殊字符
     *
     * @param str
     */
    public static Boolean hasCrossScriptRisk(String str) {
        Pattern pattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");
        return isContains(pattern, str);
    }

    /**
     * 判断一个字符串是否为数字开头
     *
     * @param str
     */
    public static Boolean isStartWithNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return isContains(pattern, str);
    }

    /**
     * 产生一个min到max范围内的随机整数
     *
     * @param min
     * @param max
     */
    public static Integer randomNum(Integer min, Integer max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /**
     * 判断str中是否包含pattern
     *
     * @param pattern 正则对象
     * @param str     需要判断的字符串
     */
    public static Boolean isContains(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }
}
