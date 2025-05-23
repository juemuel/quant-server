package com.juemuel.trend.controller;

import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import com.juemuel.trend.service.GroupService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin  //支持跨域
@RequestMapping("/group") // 统一前缀
public class GroupController {

    @Autowired
    private GroupService groupService;

    // ------------------------ 分组管理 ------------------------
    @PostMapping("/create")
    public Result<Group> createGroup(
            @RequestBody GroupCreateRequest request) {
        Group group = groupService.createGroup(
                request.getTypeCode(),
                request.getName(),
                request.getOwnerId()
        );
        return Result.success(group);
    }

    @PostMapping("/delete")
    public Result<Void> deleteGroup(
            @RequestBody GroupDeleteRequest request) {
        groupService.deleteGroup(request.getGroupId(), request.getOwnerId());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Group>> getGroupList(
            @RequestParam Long userId,
            @RequestParam(required = false) String typeCode) {
        return Result.success(
                groupService.getUserGroups(userId, typeCode)
        );
    }


    // ------------------------ 元素管理 ------------------------
    @PostMapping("/item/add")
    public Result<GroupItem> addItem(
            @RequestBody GroupItemAddRequest request) {
        GroupItem item = groupService.addItemToGroup(
                request.getGroupId(),
                request.getItemName(),
                request.getCustomData(),
                request.getOwnerId()
        );
        return Result.success(item);
    }

    @PostMapping("/item/delete")
    public Result<Void> deleteItem(
            @RequestBody GroupItemDeleteRequest request) {
        groupService.deleteItem(request.getItemId(), request.getOwnerId());
        return Result.success();
    }

    @GetMapping("/item/search")
    public Result<List<GroupItem>> searchItems(
            @RequestParam Long groupId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> tags) {
        return Result.success(
                groupService.searchItems(groupId, keyword, tags)
        );
    }

    // ------------------------ DTO定义 ------------------------
    @Data
    public static class GroupCreateRequest {
        private String typeCode;
        private String name;
        private Long ownerId;
    }

    @Data
    public static class GroupDeleteRequest {
        private Long groupId;
        private Long ownerId;
    }

    @Data
    public static class GroupItemAddRequest {
        private Long groupId;
        private String itemName;
        private Map<String, Object> customData;
        private Long ownerId;
    }

    @Data
    public static class GroupItemDeleteRequest {
        private Long itemId;
        private Long ownerId;
    }
}