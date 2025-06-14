package com.juemuel.trend.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndexData implements Serializable {
//    private static final long serialVersionUID = 1849839055107672018L; // 设置为与 Redis 中相同的值
    String date;
    float closePoint;
}
