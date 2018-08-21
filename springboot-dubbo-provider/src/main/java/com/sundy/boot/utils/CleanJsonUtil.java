package com.sundy.boot.utils;

/**
 * @author plus.wang
 * @description json 去除垃圾
 * @date 2018/4/19
 */
public class CleanJsonUtil {

    /**
     * 压缩json
     * 将格式化的json字符串压缩为一行，去掉空格、tab，并把换行符改为显式的\r\n
     * 只能处理正确json字符串，不对json字符串做校验
     *
     * @param json
     */
    public static String clean(String json) {

        if (json == null) {

            return null;
        }

        StringBuilder sb = new StringBuilder();

        boolean skip = true;// true 允许截取(表示未进入string双引号)

        boolean escaped = false;// 转义符

        for (int i = 0; i < json.length(); i++) {

            char c = json.charAt(i);

            escaped = !escaped && c == '\\';

            if (skip) {

                if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {

                    continue;
                }
            }

            sb.append(c);

            if (c == '"' && !escaped) {

                skip = !skip;
            }
        }

        return sb.toString().replaceAll("\r\n", "\\\\r\\\\n");
    }
}
