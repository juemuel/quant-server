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

import static com.juemuel.trend.util.TypeUtil.parseLong;

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

    // TIPS: 关于请求
    // 1. post用@RequestBody
    // 2. get用@RequestParam，传入参数的个数默认不会验证，可以自己写验证逻辑
    @GetMapping("/list")
    public Result<List<Group>> getGroupListWithItems(@RequestParam Map<String, Object> params) {
        System.out.println("[api] params:"+params);
        Long userId = parseLong(params.get("userId"));
        Long groupId = parseLong(params.get("groupId"));
        String keyword = (String) params.get("keyword");
        String typeCode = (String) params.get("typeCode");

        return Result.success(groupService.getGroupListWithItems(userId, groupId, keyword, typeCode));
    }


    // ------------------------ 元素管理 ------------------------
    @PostMapping("/item/add")
    public Result<GroupItem> addItem(
            @RequestBody GroupItemAddRequest request) {
        return Result.success(groupService.addGroupItem(request));
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