package com.juemuel.trend.dto;

import lombok.Data;

@Data
public class GroupItemDeleteRequest {
    private Long itemId;
    private Long ownerId;
}