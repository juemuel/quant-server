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
        // TODO:检查用户是否存在
//        if (!userDao.existsById(group.getOwnerId())) {
//            throw new IllegalArgumentException("用户不存在，ID: " + group.getOwnerId());
//        }

        // TODO:检查typeCode是否存在
//        if (!groupTypeDao.existsByTypeCode(group.getTypeCode())) {
//            throw new IllegalArgumentException("无效的typeCode: " + group.getTypeCode());
//        }
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
        System.out.println("删除分组请求 - groupId: " + groupId + ", ownerId: " + ownerId);
        Group group = groupMapper.selectGroupById(groupId);
        System.out.println("查询到的分组: " + group);
        if (group == null || !group.getOwnerId().equals(ownerId)) {
            String errorMsg = group == null ? "分组不存在" : "分组ownerId不匹配(期望:"+ownerId+",实际:"+group.getOwnerId()+")";
            System.out.println("验证失败: " + errorMsg);
            throw new RuntimeException("无权操作或分组不存在");
        }

        System.out.println("准备更新分组状态为未激活");
        group.setIsActive(false);
        int affected = groupMapper.updateGroup(group);
        System.out.println("更新影响行数: " + affected);
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
    public List<GroupItem> searchItems(Long groupId, String keyword, List<String> tags) {
        return groupItemMapper.selectItemsByTags(groupId, keyword, tags);
    }
}