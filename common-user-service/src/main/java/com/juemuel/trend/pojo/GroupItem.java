package com.juemuel.trend.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class GroupItem {
    private Long id;
    private Long groupId;
    private String name;
    private String notes;
    private String customData; // JSON 字符串
    private Integer sortOrder;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<Tag> tags; // 非数据库字段，用于业务处理
}
