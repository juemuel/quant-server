package com.juemuel.trend.controller;

import cn.hutool.json.JSONObject;
import com.juemuel.trend.pojo.User;
import com.juemuel.trend.service.UserService;
import com.juemuel.trend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin  //支持跨域
public class UserController {
    @Autowired
    UserService userService;
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        System.out.println("hell0");
        return "hello world";
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(@RequestBody JSONObject jsonObject){
        System.out.println("JSONObject: " + jsonObject);
        User user = userService.login(jsonObject);
        //若正确返回200，若错误返回400
        JSONObject result = new JSONObject();
        if(user == null){
            result.put("state",400);
        }else {
            result.put("state",200);
            // 返回携带token
            String token = TokenUtil.sign(jsonObject.getStr("userName"));
            System.out.println(token);
            result.put("token",token);
        }
        return  result;
    }
}