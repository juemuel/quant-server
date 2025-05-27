package com.juemuel.trend.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class Group {
    private Long id;
    private String typeCode;
    private String name;
    private Integer sortOrder;
    private Long ownerId;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 新增字段：嵌套 items
    private List<GroupItem> items;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", typeCode='" + typeCode + '\'' +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public List<GroupItem> getItems() {
        return items;
    }

    public void setItems(List<GroupItem> items) {
        this.items = items;
    }
}
