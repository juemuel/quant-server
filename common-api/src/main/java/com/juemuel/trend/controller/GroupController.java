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
    public Result<Group> addGroup(
            @RequestBody GroupCreateRequest request) {
        Group group = groupService.addGroup(
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
    public Result<List<Group>> getGroupListWithItems(
            @RequestParam(required = false) Long groupId,
            @RequestParam Long userId,
            @RequestParam(required = false) String keyword) {
        return Result.success(groupService.getGroupListWithItems(userId, groupId, keyword));
    }


    // ------------------------ 元素管理 ------------------------
    @PostMapping("/item/add")
    public Result<GroupItem> addItem(
            @RequestBody GroupItemAddRequest request) {
        GroupItem item = groupService.addGroupItem(
                request.getGroupId(),
                request.getItemName(),
                request.getCustomData(),
                request.getOwnerId()
        );
        return Result.success(item);
    }

    @PostMapping("/item/delete")
    public Result<Void> deleteGroupItem(
            @RequestBody GroupItemDeleteRequest request) {
        groupService.deleteGroupItem(request.getItemId(), request.getOwnerId());
        return Result.success();
    }
    @PostMapping("/item/update")
    public Result<GroupItem> updateGroupItem(@RequestBody GroupItemUpdateRequest request) {
        return Result.success(groupService.updateGroupItem(request));
    }

    // ------------------------ DTO定义 ------------------------
    // 非静态内部类（不能被 Jackson 正确反序列化）
    // 建议建立到dto包下（已转移）
}