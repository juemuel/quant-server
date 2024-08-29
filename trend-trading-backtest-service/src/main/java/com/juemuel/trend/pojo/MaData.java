package com.juemuel.trend.pojo;

public class MaData {
    private String date;
    private float value;
    public MaData(String date, float value) {
        this.date = date;
        this.value = value;
    }
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
    @Override
    public String toString() {
        return "MA{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
