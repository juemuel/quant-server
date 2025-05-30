package com.juemuel.trend.service;
import com.juemuel.trend.controller.GroupController;
import com.juemuel.trend.dao.GroupMapper;
import com.juemuel.trend.dao.GroupItemMapper;
import com.juemuel.trend.dao.GroupTypeMapper;
import com.juemuel.trend.dao.UserDAO;
import com.juemuel.trend.dto.*;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import com.juemuel.trend.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GroupServiceImpl implements GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupItemMapper groupItemMapper;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private GroupTypeMapper groupTypeMapper;

    /**
     * 创建分组
     * @param typeCode
     * @param name
     * @param ownerId
     * @return
     */
    @Override
    @Transactional
    public Group addGroup(String typeCode, String name, Long ownerId) {
        if (!userDao.existsById(ownerId)) {
            throw new IllegalArgumentException("用户不存在，ID: " + ownerId);
        }
        if (!groupTypeMapper.existsByTypeCode(typeCode)) {
            throw new IllegalArgumentException("无效的typeCode: " + typeCode);
        }
        Group group = new Group();
        group.setTypeCode(typeCode);
        group.setName(name);
        group.setOwnerId(ownerId);
        group.setIsActive(true);
        groupMapper.insertGroup(group);
        return group;
    }

    /**
     * 删除分组
     * @param groupId
     * @param ownerId
     */
    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long ownerId) {
        Group group = groupMapper.selectGroupById(groupId);
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("无权操作或分组不存在");
        }

        // 逻辑删除：设置 is_active = false
        group.setIsActive(false);
        groupMapper.updateGroup(group);
    }

    /**
     * 更新分组
     * @param request
     * @return
     */
    @Override
    @Transactional
    public Group updateGroup(GroupUpdateRequest request) {
        Group group = groupMapper.selectGroupById(request.getGroupId());
        if (group == null || !group.getOwnerId().equals(request.getOwnerId())) {
            throw new RuntimeException("无权操作或分组项不存在");
        }

        // 更新字段
        group.setTypeCode(request.getTypeCode());
        group.setName(request.getName());

        groupMapper.updateGroup(group); // 使用已有的 mapper 方法
        return group;
    }

    /**
     * 添加分组项
     * @param groupId
     * @param itemName
     * @param customData
     * @param ownerId
     * @return
     */
    @Override
    @Transactional
    public GroupItem addGroupItem(Long groupId, String itemName, Map<String, Object> customData, Long ownerId) {
        Group group = groupMapper.selectGroupById(groupId);
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("无权操作或分组不存在");
        }

        GroupItem item = new GroupItem();
        item.setGroupId(groupId);
        item.setName(itemName);
        item.setCustomData(customData);
//        item.setIsActive(true);
//        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        groupItemMapper.insertGroupItem(item);
        return item;
    }

    /**
     * 删除分组项
     * @param itemId
     * @param ownerId
     */
    @Override
    @Transactional
    public void deleteGroupItem(Long itemId, Long ownerId) {
        GroupItem item = groupItemMapper.selectItemById(itemId);
        if (item == null) {
            throw new RuntimeException("分组项不存在");
        }

        Group group = groupMapper.selectGroupById(item.getGroupId());
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("无权操作或分组不属于你");
        }

        // 逻辑删除：设置 is_active = false
        item.setIsActive(false);
        groupItemMapper.updateGroupItem(item);
    }

    /**
     *  更新分组项
     * @param request
     * @return
     */
    @Override
    @Transactional
    public GroupItem updateGroupItem(GroupItemUpdateRequest request) {
        GroupItem groupItem = groupItemMapper.selectItemById(request.getItemId());
        if (groupItem == null) {
            throw new RuntimeException("分组项不存在");
        }

        Group group = groupMapper.selectGroupById(groupItem.getGroupId());
        if (group == null || !group.getOwnerId().equals(request.getOwnerId())) {
            throw new RuntimeException("无权操作，不属于你的分组");
        }

        groupItem.setName(request.getItemName());
        groupItem.setCustomData(request.getCustomData());
        groupItem.setNotes(request.getNotes());

        groupItemMapper.updateGroupItem(groupItem); // 确保已有该方法
        return groupItem;
    }

    /**
     * 获取用户的分组列表，包含分组下的items
     * @param userId
     * @param keyword
     * @return
     */
    @Override
    public List<Group> getGroupListWithItems(Long userId, Long groupId, String keyword) {
        // Step 1: 获取用户的分组列表
        List<Group> groups = groupMapper.selectGroupsByUser(userId, null); // typeCode 可为 null
        // Step 2: 根据 groupId 过滤
        if (groupId != null) {
            groups.removeIf(g -> !g.getId().equals(groupId));
        }
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }
        // Step 3: 批量获取每个 group 下的 items，并应用 keyword 过滤
        for (Group group : groups) {
            List<GroupItem> items = groupItemMapper.selectGroupItems(group.getId(), keyword);
            if (items != null && !items.isEmpty()) {
                group.setItems(items);
            } else {
                group.setItems(Collections.emptyList());
            }
        }
        return groups;
    }
}