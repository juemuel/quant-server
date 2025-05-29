package com.juemuel.trend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class GroupItemUpdateRequest {
    private Long itemId;
    private Long groupId;
    private String itemName;
    private String notes;
    private Map<String, Object> customData;
    private Long ownerId;
}