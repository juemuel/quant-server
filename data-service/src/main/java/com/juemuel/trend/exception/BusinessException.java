package com.juemuel.trend.exception;

import com.juemuel.trend.http.Result;
import lombok.Data;

/**
 * 自定义业务异常（带错误码和消息）
 */
@Data
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 快速构建 Result 响应
     */
    public Result<Void> toResult() {
        return Result.error(code, message);
    }
}
