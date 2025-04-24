package com.juemuel.trend.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class GroupType {
    private Long id;
    private String typeCode;
    private String typeName;
    private String config; // JSON 字符串
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
