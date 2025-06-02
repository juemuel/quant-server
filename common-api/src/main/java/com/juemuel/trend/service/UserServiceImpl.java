package com.juemuel.trend.service;


import com.juemuel.trend.dao.UserMapper;
import com.juemuel.trend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// 记得这里的声明哦
@Service
public class UserServiceImpl implements UserService {

    //引入 dao接口
    @Autowired
    UserMapper userMapper;

    //重写login实现类
    @Override
    public User login(Map<String,Object> userInfo){
        System.out.println("login service" + userInfo);
        User user = userMapper.loginSel(userInfo);
        if (user == null) {
            System.out.println("未找到用户，请检查用户名或密码");
        } else {
            System.out.println("找到用户：" + user.getUserName());
        }
        return user;
    }
    // TODO:实现注册等功能
}