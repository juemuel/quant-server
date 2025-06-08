package com.juemuel.trend.controller;

import cn.hutool.json.JSONObject;
import com.juemuel.trend.http.Result;
import com.juemuel.trend.pojo.User;
import com.juemuel.trend.service.UserService;
import com.juemuel.trend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials = "true")  //支持跨域
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        System.out.println("hell0");
        return "hello world";
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<Object> login(@RequestBody JSONObject jsonObject){
//        System.out.println("JSONObject: " + jsonObject);
        User user = userService.login(jsonObject);
        //若正确返回200，若错误返回400
        if(user == null){
            return Result.error(400, "登录失败");
        }
        String token = TokenUtil.sign(jsonObject.getStr("userName"));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("userName", user.getUserName());
        data.put("role", user.getRole()); // 可根据实际业务设置角色
        return Result.success(data);
    }
}