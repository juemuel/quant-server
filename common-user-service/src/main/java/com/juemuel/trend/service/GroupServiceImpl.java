package com.juemuel.trend.service;
import com.juemuel.trend.dao.GroupMapper;
import com.juemuel.trend.dao.GroupItemMapper;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import com.juemuel.trend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupItemMapper groupItemMapper;

    @Override
    @Transactional
    public Group createGroup(String typeCode, String name, Long ownerId) {
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
            throw new RuntimeException("无权操作或分组不存在");
        }
        group.setIsActive(false);
        groupMapper.updateGroup(group);
    }

    @Override
    public List<Group> getUserGroups(Long userId, String typeCode) {
        return groupMapper.selectGroupsByUser(userId, typeCode);
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
    public void removeItem(Long itemId, Long ownerId) {
        GroupItem item = groupItemMapper.selectItemById(itemId);
        if (item != null) {
            Group group = groupMapper.selectGroupById(item.getGroupId());
            if (group.getOwnerId().equals(ownerId)) {
                groupItemMapper.deleteItem(itemId);
            }
        }
    }

    @Override
    public List<GroupItem> searchItems(Long groupId, String keyword, List<String> tags) {
        return groupItemMapper.selectItemsByTags(groupId, keyword, tags);
    }
}