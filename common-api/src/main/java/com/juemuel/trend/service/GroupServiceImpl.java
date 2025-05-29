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
    @Override
    @Transactional
    public Group createGroup(String typeCode, String name, Long ownerId) {
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

    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long ownerId) {
        Group group = groupMapper.selectGroupById(groupId);
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            String errorMsg = group == null ? "分组不存在" : "分组ownerId不匹配(期望:"+ownerId+",实际:"+group.getOwnerId()+")";
            System.out.println("验证失败: " + errorMsg);
            throw new RuntimeException("无权操作或分组不存在");
        }
        group.setIsActive(false);
        int affected = groupMapper.updateGroup(group);
    }
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
     * 获取用户的分组列表，包含分组下的items
     * @param userId
     * @param typeCode
     * @param keyword
     * @param tags
     * @return
     */
    @Override
    public List<Group> getGroupsWithItems(Long userId, String typeCode, String keyword, List<String> tags) {
        List<Group> groups = groupMapper.selectGroupsByUser(userId, typeCode);

        if ((keyword != null && !keyword.isEmpty()) || (tags != null && !tags.isEmpty())) {
            for (Group group : groups) {
                List<GroupItem> items = groupItemMapper.selectItemsByTags(group.getId(), keyword, tags);
                group.setItems(items);
            }
        } else {
            for (Group group : groups) {
                List<GroupItem> items = groupItemMapper.selectByGroupId(group.getId());
                group.setItems(items);
            }
        }

        return groups;
    }

    @Override
    @Transactional
    public GroupItem addItemToGroup(Long groupId, String itemName, Map<String, Object> customData, Long ownerId) {
        Group group = groupMapper.selectGroupById(groupId);
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("无权操作或分组不存在");
        }

        GroupItem item = new GroupItem();
        item.setGroupId(groupId);
        item.setName(itemName);
        item.setCustomData(customData);
        groupItemMapper.insertGroupItem(item);
        return item;
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long ownerId) {
        GroupItem item = groupItemMapper.selectItemById(itemId);
        if (item != null) {
            Group group = groupMapper.selectGroupById(item.getGroupId());
            if (group.getOwnerId().equals(ownerId)) {
                groupItemMapper.deleteItem(itemId, ownerId);
            }
        }
    }
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
    @Override
    public List<GroupItem> searchItems(Long groupId, String keyword, List<String> tags) {
        if (tags == null) {
            tags = Collections.emptyList();
        }
        return groupItemMapper.selectItemsByTags(groupId, keyword, tags);
    }
}