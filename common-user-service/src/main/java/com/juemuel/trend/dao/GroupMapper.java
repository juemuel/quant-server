package com.juemuel.trend.dao;

import com.juemuel.trend.pojo.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;
// 使用注解还是在mapper.xml中实现根据习惯来，一般简单SQL用注解（如 @Select）、复杂SQL用XML（如动态SQL）
@Mapper
public interface GroupMapper {
    /**
     * 创建分组（注解）
     * @param group
     * @return
     */
    @Insert("INSERT INTO `group` (type_code, name, owner_id) VALUES (#{typeCode}, #{name}, #{ownerId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGroup(Group group);

    /**
     * 删除分组（注解）
     * @param groupId
     * @param ownerId
     * @return
     */
    @Delete("DELETE FROM `group` WHERE id = #{groupId} AND owner_id = #{ownerId}")
    int deleteGroup(@Param("groupId") Long groupId, @Param("ownerId") Long ownerId);

    /**
     * 更新分组（软删除/排序）(注解）
     * @param group
     * @return
     */
    @Update("UPDATE `group` SET name=#{name}, sort_order=#{sortOrder}, is_active=#{isActive} WHERE id=#{id}")
    int updateGroup(Group group);

    /**
     * 查询用户拥有的分组（带类型过滤）（xml）
     * @param userId
     * @param typeCode
     * @return
     */
    List<Group> selectGroupsByUser(@Param("userId") Long userId,
                                   @Param("typeCode") String typeCode);

    /**
     * 根据分组ID查询分组信息（注解）
     * @param groupId
     * @return
     */
    @Select("SELECT * FROM `group` WHERE id = #{groupId}")
    Group selectGroupById(Long groupId);
}
