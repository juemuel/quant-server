package com.juemuel.trend.dao;

import com.juemuel.trend.pojo.GroupItem;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupItemMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGroupItem(GroupItem item);
    int updateGroupItem(GroupItem item);
    List<GroupItem> selectItemsByTags(@Param("groupId") Long groupId,
                                      @Param("keyword") String keyword,
                                      @Param("tags") List<String> tags);
}
