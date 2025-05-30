package com.juemuel.trend.dao;

import com.juemuel.trend.pojo.Group;
import com.juemuel.trend.pojo.GroupItem;
import com.juemuel.trend.pojo.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;
// 使用注解还是在mapper.xml中实现根据习惯来，一般简单SQL用注解（如 @Select）、复杂SQL用XML（如动态SQL）
public interface GroupItemMapper {
    /**
     * 增加组元素（注解）
     * @param item
     * @return
     */
    @Insert("INSERT INTO group_item (group_id, name, custom_data) " +
            "VALUES (#{groupId}, #{name}, #{customData, typeHandler=com.juemuel.trend.handler.JsonTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGroupItem(GroupItem item);

    /**
     * 删除/更新组元素（xml）
     * @param item
     * @return
     */
    int updateGroupItem(GroupItem item);

    /**
     * 查询某个组元素（注解）
     * @param itemId
     * @return
     */
    @Select("SELECT * FROM group_item WHERE id = #{itemId}")
    GroupItem selectItemById(Long itemId);

    /**
     * 搜索组内元素（支持关键词过滤）
     * @param groupId 可选，指定组ID
     * @param keyword 可选，关键词匹配 name/notes/custom_data
     * @return
     */
    List<GroupItem> selectGroupItems(@Param("groupId") Long groupId,
                                     @Param("keyword") String keyword);

}
