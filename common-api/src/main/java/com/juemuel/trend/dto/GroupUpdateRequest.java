package com.juemuel.trend.dto;

import lombok.Data;

@Data
public class GroupUpdateRequest {
    private Long groupId;
    private String typeCode;
    private String name;
    private Long ownerId;
}