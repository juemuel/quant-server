package com.juemuel.trend.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeReference;

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
                                    Map<String, Object> parameter, JdbcType jdbcType) throws JsonProcessingException, SQLException {
        ps.setString(i, objectMapper.writeValueAsString(parameter));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
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