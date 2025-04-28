package com.juemuel.trend.dao;

public interface GroupTypeMapper {
    /**
     * 检查分组类型是否存在
     */
    boolean existsByTypeCode(String typeCode);
}
