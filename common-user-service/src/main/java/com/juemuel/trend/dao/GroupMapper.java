package com.juemuel.trend.dao;

import com.juemuel.trend.pojo.Group;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupMapper {
    // 创建分组
    // 返回自增主键+ 插入注解和mapper.xml中只需要保留一个即可，此处保留 XML 定义，删除接口注解
    // 使用注解还是在mapper.xml中实现根据习惯来，一般简单SQL用注解（如 @Select）、复杂SQL用XML（如动态SQL）
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGroup(Group group);

    // 更新分组（软删除/排序）
    int updateGroup(Group group);

    // 查询用户拥有的分组（带类型过滤）
    List<Group> selectGroupsByUser(@Param("userId") Long userId,
                                   @Param("typeCode") String typeCode);

}
