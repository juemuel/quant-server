package com.juemuel.trend.pojo;

import java.io.Serializable;

public class Profit implements Serializable {

    String date;
    float value;

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
    }

}