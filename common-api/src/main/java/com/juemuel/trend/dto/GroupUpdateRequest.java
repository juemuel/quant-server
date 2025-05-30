package com.juemuel.trend.dto;

import lombok.Data;

@Data
public class GroupUpdateRequest {
    private Long groupId;
    private Long ownerId;
    private String typeCode;
    private String name;
}