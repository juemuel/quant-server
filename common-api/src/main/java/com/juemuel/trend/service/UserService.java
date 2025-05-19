package com.juemuel.trend.service;



import com.juemuel.trend.pojo.User;

import java.util.Map;

public interface UserService {

    //登录
    User login(Map<String,Object> userInfo);
}