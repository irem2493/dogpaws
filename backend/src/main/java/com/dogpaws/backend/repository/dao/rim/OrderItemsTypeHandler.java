package com.dogpaws.backend.repository.dao.rim;

import com.dogpaws.backend.dto.rim.OrderItemDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(List.class)
public class OrderItemsTypeHandler extends BaseTypeHandler<List<OrderItemDto>> {
    private final ObjectMapper objectMapper;

    public OrderItemsTypeHandler() {
        this.objectMapper = new ObjectMapper();
        // JSON 처리를 위한 설정 추가
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<OrderItemDto> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<OrderItemDto> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null) {
            return null;
        }
        try {
            // JSON 문자열을 디버그 로그로 출력
            System.out.println("Received JSON: " + json);
            return objectMapper.readValue(json, new TypeReference<List<OrderItemDto>>() {});
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Error converting JSON to OrderItemDto list", e);
        }
    }

    @Override
    public List<OrderItemDto> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getNullableResult(rs, rs.getMetaData().getColumnName(columnIndex));
    }

    @Override
    public List<OrderItemDto> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<OrderItemDto>>() {});
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting JSON to OrderItemDto list", e);
        }
    }

    private String toJson(List<OrderItemDto> orderItems) throws SQLException {
        try {
            return objectMapper.writeValueAsString(orderItems);
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting OrderItemDto list to JSON", e);
        }
    }
}