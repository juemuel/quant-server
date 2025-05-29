package com.juemuel.trend.dto;

import lombok.Data;

@Data
public class GroupCreateRequest {
    private String typeCode;
    private String name;
    private Long ownerId;
}