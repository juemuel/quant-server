package com.juemuel.trend.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Group {
    private Long id;
    private String typeCode;
    private String name;
    private Integer sortOrder;
    private Long ownerId;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
