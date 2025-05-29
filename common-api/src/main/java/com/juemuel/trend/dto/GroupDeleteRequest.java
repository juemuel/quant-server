package com.juemuel.trend.dto;

import lombok.Data;

@Data
public class GroupDeleteRequest {
    private Long groupId;
    private Long ownerId;
}