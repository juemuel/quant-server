package com.juemuel.trend.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IndexData implements Serializable {
    String date;
    float closePoint;
}
