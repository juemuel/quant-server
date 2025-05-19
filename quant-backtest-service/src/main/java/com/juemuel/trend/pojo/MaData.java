package com.juemuel.trend.pojo;

import java.util.Collections;
import java.util.List;

public class MaData {
    private String date;
    private List<Float> value;
    public MaData(String date, List<Float> value) {
        this.date = date;
        this.value = value;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public List<Float> getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = Collections.singletonList(value);
    }
    @Override
    public String toString() {
        return "MA{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
