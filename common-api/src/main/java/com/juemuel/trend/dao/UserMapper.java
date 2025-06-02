package com.juemuel.trend.dao;

import com.juemuel.trend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    //登录，这里的loginSel对应于UserMapper.xml中的id="loginSel"方法
    /**
     * 登录
     * @param userInfo
     * @return
     */
    User loginSel(Map<String,Object> userInfo);
    /**
     * 检查用户是否存在
     */
    boolean existsById(Long id);
}