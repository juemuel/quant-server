package com.juemuel.trend.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

@MappedTypes(Map.class)
public class JsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new SQLException("JSON 序列化失败", e); // 将 Jackson 异常转换为 SQLException
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap(); // 返回空 Map 而不是抛异常
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new SQLException("Error parsing JSON from column: " + columnName, e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return Collections.emptyMap();
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("JSON 解析失败");
        }
    }
}