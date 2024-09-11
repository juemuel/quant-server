package com.juemuel.trend.service;


import com.juemuel.trend.dao.UserDAO;
import com.juemuel.trend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

// 记得这里的声明哦
@Service
public class UserServiceImpl implements UserService {

    //引入 dao接口
    @Autowired
    UserDAO userDAO;

    //重写login实现类
    @Override
    public User login(Map<String,Object> userInfo){
        System.out.println("login service" + userInfo);
        return userDAO.loginSel(userInfo);
    }
}