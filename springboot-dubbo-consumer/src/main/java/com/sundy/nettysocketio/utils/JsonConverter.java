package com.sundy.nettysocketio.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author plus.wang
 * @description org.json 对象转换
 * @date 2018/4/20
 */
public class JsonConverter {

    public static JSONObject objectToJSONObject(Object object) {

        try {

            String jsonString = new ObjectMapper().writeValueAsString(object);

            return new JSONObject(jsonString);

        } catch (JSONException | JsonProcessingException e) {

            e.printStackTrace();

            return null;
        }
    }

    public static JSONArray objectToJSONArray(Object object) {

        try {

            String jsonString = new ObjectMapper().writeValueAsString(object);

            return new JSONArray(jsonString);

        } catch (JSONException | JsonProcessingException e) {

            e.printStackTrace();

            return null;
        }
    }

    public static <T> T jsonObjectToObject(Object jsonObject, Class<T> clazz) {

        try {

            return new ObjectMapper().readValue(jsonObject.toString(), clazz);

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }
}
