package com.juemuel.trend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class GroupItemAddRequest {
    private Long groupId;
    private String itemName;
    private Map<String, Object> customData;
    private Long ownerId;
}