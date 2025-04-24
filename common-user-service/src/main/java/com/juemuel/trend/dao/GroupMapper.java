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
    // 返回自增主键+插入注解
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO `group` (type_code, name, owner_id) VALUES (#{typeCode}, #{name}, #{ownerId})")
    int insertGroup(Group group);

    // 更新分组（软删除/排序）
    int updateGroup(Group group);

    // 查询用户拥有的分组（带类型过滤）
    List<Group> selectGroupsByUser(@Param("userId") Long userId,
                                   @Param("typeCode") String typeCode);

}
