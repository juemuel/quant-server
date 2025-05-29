package com.juemuel.trend.controller;

import com.juemuel.trend.dto.*;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import com.juemuel.trend.service.GroupService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
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
    @PostMapping("/update")
    public Result<Group> updateGroup(@RequestBody GroupUpdateRequest request) {
        return Result.success(groupService.updateGroup(request));
    }

    @GetMapping("/list")
    public Result<List<Group>> getGroupsWithItems(
            @RequestParam Long userId,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> tags) {
        return Result.success(groupService.getGroupsWithItems(userId, typeCode, keyword, tags));
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
        if (tags == null) {
            tags = Collections.emptyList();
        }
        return Result.success(groupService.searchItems(groupId, keyword, tags));
    }
    @PostMapping("/item/update")
    public Result<GroupItem> updateGroupItem(@RequestBody GroupItemUpdateRequest request) {
        return Result.success(groupService.updateGroupItem(request));
    }

    // ------------------------ DTO定义 ------------------------
    // 非静态内部类（不能被 Jackson 正确反序列化）
    // 建议建立到dto包下（已转移）
}