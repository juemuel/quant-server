package com.juemuel.trend.service;
import com.juemuel.trend.controller.GroupController;
import com.juemuel.trend.dto.*;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import java.util.List;
import java.util.Map;

public interface GroupService {
    // 分组操作：增、删、查
    Group createGroup(String typeCode, String name, Long ownerId);
    void deleteGroup(Long groupId, Long ownerId);
    List<Group> getGroupsWithItems(Long userId, String typeCode, String keyword, List<String> tags);
    Group updateGroup(GroupUpdateRequest request);
    // 组内元素操作：增、删、查
    GroupItem addItemToGroup(Long groupId, String itemName, Map<String, Object> customData, Long ownerId);
    void deleteItem(Long itemId, Long ownerId);
    List<GroupItem> searchItems(Long groupId, String keyword, List<String> tags);
    GroupItem updateGroupItem(GroupItemUpdateRequest request);
}