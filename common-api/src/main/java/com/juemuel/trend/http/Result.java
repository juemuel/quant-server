package com.juemuel.trend.http;

import lombok.Data;

@Data
public class Result<T> {
    private int code;    // 状态码 (200表示成功)
    private String msg;  // 提示信息
    private T data;      // 响应数据

    // 成功响应 (带数据)
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    // 成功响应 (无数据)
    public static <T> Result<T> success() {
        return success(null);
    }

    // 错误响应
    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}