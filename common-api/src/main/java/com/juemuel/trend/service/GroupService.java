package com.juemuel.trend.service;
import com.juemuel.trend.controller.GroupController;
import com.juemuel.trend.dto.*;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import java.util.List;
import java.util.Map;

public interface GroupService {
    // 分组操作：增、删、查
    Group addGroup(String typeCode, String name, Long ownerId);
    void deleteGroup(Long groupId, Long ownerId);
    Group updateGroup(GroupUpdateRequest request);
    // 组内元素操作：增、删、查
    GroupItem addGroupItem(Long groupId, String itemName, Map<String, Object> customData, Long ownerId);
    void deleteGroupItem(Long itemId, Long ownerId);
    GroupItem updateGroupItem(GroupItemUpdateRequest request);

    List<Group> getGroupListWithItems(Long userId, Long groupId, String keyword);
}