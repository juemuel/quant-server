package com.juemuel.trend.controller;

import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin  //支持跨域
public class UserController {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        System.out.println("hell0");
        return "hello world";
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(@RequestBody JSONObject jsonObject){
        String userName = jsonObject.getStr("userName");
        String password = jsonObject.getStr("password");
        //获取用户名，并打印
        System.out.println(userName);
        System.out.println(password);
        JSONObject result = new JSONObject();
        //若正确返回200，若错误返回400
        if(userName.equals("xiaoming")&& password.equals("123456")){
            result.put("state",200);
        }else{
            result.put("state",400);
        }
        return  result;
    }
}