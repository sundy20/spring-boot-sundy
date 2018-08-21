package com.sundy.boot.utils;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * @author plus.wang
 * @description properties 文件加载
 * @date 2018/5/16
 */
public class ResourceUtil {

    public static Map<String, String> readProperties(String resourceName) {
        Properties properties = new Properties();
        URL resource = Resources.getResource(resourceName);
        try {
            properties.load(new InputStreamReader(resource.openStream(), "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Maps.fromProperties(properties);
    }

}
