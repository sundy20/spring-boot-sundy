package com.sundy.boot.inventory.DO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Map.class})
public class MapObjectTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Map<String, Object> listLong,
                                    JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, serializeMapObject(listLong));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        return deserializeMapObject(value);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        return deserializeMapObject(value);
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        return deserializeMapObject(value);
    }

    public static Map<String, Object> deserializeMapObject(String content) {
        if (Strings.isNullOrEmpty(content)) {
            return Maps.newHashMap();
        }
        return JSON.parseObject(content, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String serializeMapObject(Map<String, Object> map) {
        if (map == null) {
            return "{}";
        } else {
            return JSON.toJSONString(map);
        }
    }
}
